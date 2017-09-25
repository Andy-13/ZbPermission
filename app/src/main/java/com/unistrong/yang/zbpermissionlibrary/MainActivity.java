package com.unistrong.yang.zbpermissionlibrary;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.unistrong.yang.zb_permission.Permission;
import com.unistrong.yang.zb_permission.ZbPermissionFail;
import com.unistrong.yang.zb_permission.ZbPermissionSuccess;
import com.unistrong.yang.zb_permission.ZbPermission;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final int REQUEST_CONTACT = 50;
    private final int REQUEST_STORAGE = 100;
    private final int REQUEST_CAMERA = 200;
    private Button bt_request_storage;
    private Button bt_request_camera;
    private Button bt_request_contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();
    }

    private void initView() {
        bt_request_contact = (Button) findViewById(R.id.bt_request_contact);
        bt_request_camera = (Button) findViewById(R.id.bt_request_camera);
        bt_request_storage = (Button) findViewById(R.id.bt_request_storage);
    }

    private void setListener() {
        bt_request_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZbPermission.with(MainActivity.this)
                        .addRequestCode(REQUEST_CONTACT)
                        .permissions(Manifest.permission.READ_CONTACTS, Manifest.permission.RECEIVE_SMS, Manifest.permission.WRITE_CONTACTS)
                        .request(/*new ZbPermission.ZbPermissionCallback() {
                            @Override
                            public void permissionSuccess(int requestCode) {
                                Toast.makeText(MainActivity.this, "成功授予Contact权限: " + requestCode, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void permissionFail(int requestCode) {
                                Toast.makeText(MainActivity.this, "成功授予Contact拍照权限: " + requestCode, Toast.LENGTH_SHORT).show();
                            }
                        }*/);
            }
        });

        bt_request_storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZbPermission.needPermission(MainActivity.this, REQUEST_STORAGE, Permission.STORAGE);
            }
        });

        bt_request_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZbPermission.needPermission(MainActivity.this, REQUEST_CAMERA, Permission.CAMERA, new ZbPermission.ZbPermissionCallback() {
                    @Override
                    public void permissionSuccess(int requestCode) {
                        Toast.makeText(MainActivity.this, "成功授予拍照权限: " + requestCode, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void permissionFail(int requestCode) {
                        Toast.makeText(MainActivity.this, "授予拍照权限失败: " + requestCode, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @ZbPermissionSuccess(requestCode = REQUEST_STORAGE)
    public void permissionSuccess() {
        Toast.makeText(MainActivity.this, "成功授予读写权限注解" , Toast.LENGTH_SHORT).show();
    }

    @ZbPermissionFail(requestCode = REQUEST_STORAGE)
    public void permissionFail() {
        Toast.makeText(MainActivity.this, "授予读写权限失败注解" , Toast.LENGTH_SHORT).show();
    }

    @ZbPermissionSuccess(requestCode = REQUEST_CONTACT)
    public void permissionSuccessContact() {
        Toast.makeText(MainActivity.this, "成功授予Contact权限注解" , Toast.LENGTH_SHORT).show();
    }

    @ZbPermissionFail(requestCode = REQUEST_CONTACT)
    public void permissionFailContact() {
        Toast.makeText(MainActivity.this, "授予Contact权限失败注解" , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ZbPermission.onRequestPermissionsResult(MainActivity.this, requestCode, permissions, grantResults);
    }
}
