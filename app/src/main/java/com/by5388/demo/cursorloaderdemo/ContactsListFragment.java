package com.by5388.demo.cursorloaderdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

/**
 * @author Administrator  on 2020/8/19.
 */

public class ContactsListFragment extends ListFragment
        implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {


    private static final String TAG = "ContactsList";
    private static final int REQUEST_CODE_READ_CONTACTS = 100;
    private static final int ID_LOAD_CONTACTS = 1;
    private View mViewRoot;
    private Button mButton;

    private ContactsAdapter mAdapter;


    public static ContactsListFragment newInstance() {
        return new ContactsListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Context context = requireContext();
        mViewRoot = view.findViewById(R.id.root_container);
        mButton = view.findViewById(R.id.button_start);
        mButton.setOnClickListener(this);
        mAdapter = new ContactsAdapter(context);
        setListAdapter(mAdapter);
        if (grantedContactsPermission()) {
            startLoadContacts();
        }else{
            mButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mButton) {
            if (grantedContactsPermission()) {
                mButton.setVisibility(View.GONE);
                startLoadContacts();
                return;
            }
            final FragmentActivity activity = getActivity();
            if (activity == null) {
                Log.e(TAG, "onClick: activity == null");
                return;
            }
            final boolean showRequestPermissionRationale =
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CONTACTS);
            if (showRequestPermissionRationale) {
                Snackbar.make(mViewRoot, "需要读取联系人权限", Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestPermissions();
                            }
                        }).show();
            }else{
                requestPermissions();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_CONTACTS) {
            if (grantResults.length == 0) {
                Toast.makeText(requireContext(), "用户取消授权", Toast.LENGTH_SHORT).show();
            }else if (grantedPermission(grantResults)) {
                mButton.setVisibility(View.GONE);
                startLoadContacts();
            }else{
                final FragmentActivity activity = getActivity();
                if (activity == null) {
                    Log.e(TAG, "onRequestPermissionsResult: activity == null");
                    return;
                }
                final boolean showRequestPermissionRationale =
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CONTACTS);
                if (showRequestPermissionRationale) {
                    Snackbar.make(mViewRoot, "需要读取联系人权限，禁用后将不能正常使用", Snackbar.LENGTH_INDEFINITE)
                            .setAction(android.R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    requestPermissions();
                                }
                            }).show();
                }else{
                    Snackbar.make(mViewRoot, "用户已禁止权限申请，请打开设置授权", Snackbar.LENGTH_INDEFINITE)
                            .setAction(android.R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openSettings();
                                }
                            }).show();
                }
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == ID_LOAD_CONTACTS) {
            if (!grantedContactsPermission()) {
                return null;
            }
            return new CursorLoader(requireContext(),
                    IContactsApi.CONTACTS_URI,
                    IContactsApi.CONTACTS_PROJECTION,
                    null,
                    null,
                    IContactsApi.SORT_KEY
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (mAdapter == null) {
            return;
        }
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (mAdapter == null) {
            return;
        }
        mAdapter.swapCursor(null);
    }

    private boolean grantedPermission(@NonNull int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        if (grantedContactsPermission()) {
            return;
        }
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
    }

    private void openSettings() {
        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
        startActivity(intent);
    }

    private void startLoadContacts() {
        if (!grantedContactsPermission()) {
            return;
        }
        // TODO: 2020/8/19
        LoaderManager.getInstance(this).initLoader(ID_LOAD_CONTACTS, null, this);
    }


    private boolean grantedContactsPermission() {
        return PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS);
    }
}
