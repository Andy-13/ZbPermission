# ZbPermission

Android 8.0 7.0 6.0 动态权限管理
1.Android6.0之后运行时权限策略变化

从Android6.0（API23）开始，对系统权限做了很大的改变，在之前用户安装app前，只是把app需要的使用的权限列出来告知用户一下，app安装后都可以访问这些权限。从6.0开始，一些敏感权限需要在使用是动态申请，并且用户可以选择拒绝授权访问这些权利，已授予过的权限，用户也可以去app设置界面去关闭授权。这对用户来说提高了安全性，可以防止一些应用恶意访问用户数据，但是对于开发来说，也增加了不少的工作量，这块不做适配处理的话，app在访问权限的时候容易出现crash。

2.权限等级
权限主要分为normal、dangerous、signature和signatureOrSystem四个等级，常规情况下我们只需要了解前两种，即正常权限和危险权限。
2.1、正常权限

正常权限涵盖应用需要访问其沙盒外部数据或资源，但对用户隐私或其他应用操作风险很小的区域。应用声明其需要正常权限，系统会自动授予该权限。例如设置时区，只要应用声明过权限，系统就直接授予应用此权限。

2.2、危险权限

危险权限涵盖应用需要涉及用户隐私信息的数据或资源，或者可能对用户存储的数据或其他应用的操作产生影响的区域。例如读取用户联系人，在6.0以上系统中，需要在运行时明确向用户申请权限。




3、运行时请求权限

3.1、检查权限

应用每次需要危险权限时，都要判断应用目前是否有该权限。兼容库中已经做了封装，只需要通过下面代码即可：

int permissionCheck = ContextCompat.checkSelfPermission(thisActivity,    Manifest.permission.WRITE_CALENDAR);
 如果有权限则返回PackageManager.PERMISSION_GRANTED，否则返回PackageManager。PERMISSION_DENIED。
3.2、请求权限

当应用需要某个权限时，可以申请获取权限，这时会有弹出一个系统标准Dialog提示申请权限，此Diolog不能定制，用户同意或者拒绝后会通过方法onRequestPermissionsResult()返回结果。
 ActivityCompat.requestPermissions(thisActivity,
        new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE);
3.3、处理权限请求响应

当用户处理权限请求后，系统会回调申请权限的Activity的onRequestPermissionsResult()方法，只需要覆盖此方法，就能获得返回结果.
 @Override
public void onRequestPermissionsResult(int requestCode,
                                       String permissions[], int[] grantResults) {
 
}

4.Android O的运行时权限策略变化

4.1在 Android O 之前，如果应用在运行时请求权限并且被授予该权限，系统会错误地将属于同一权限组并且在清单中注册的其他权限也一起授予应用。
4.2对于针对Android O的应用，此行为已被纠正。系统只会授予应用明确请求的权限。然而一旦用户为应用授予某个权限，则所有后续对该权限组中权限的请求都将被自动批准,但是若没有请求相应的权限而进行操作的话就会出现应用crash的情况.
例如，假设某个应用在其清单中列出READ_EXTERNAL_STORAGE和WRITE_EXTERNAL_STORAGE。应用请求READ_EXTERNAL_STORAGE，并且用户授予了该权限，如果该应用针对的是API级别24或更低级别，系统还会同时授予WRITE_EXTERNAL_STORAGE，因为该权限也属于STORAGE权限组并且也在清单中注册过。如果该应用针对的是Android O，则系统此时仅会授予READ_EXTERNAL_STORAGE，不过在该应用以后申请WRITE_EXTERNAL_STORAGE权限时，系统会立即授予该权限，而不会提示用户。但是若没有申请WRITE_EXTERNAL_STORAGE权限，而去进行写存储卡的操作的时候，就会引起应用的崩溃。
4.3对Android O运行时权限策略变化的应对方案

针对Android O 的运行是的权限特点，我们可以在申请权限的时候要申请权限数组，而不是单一的某一个权限。所以按照上面的危险权限列表我们给系统权限进行分类，把一个组的常量放到数组中，并根据系统版本进行赋值。

/**
 * Created by Yang on 2017/9/20.
 * desc: 由于Android8.0的限制 最好的做法是申请权限的时候一组一组的申请
 */

public final class Permission {

    public static final String[] CALENDAR;
    public static final String[] CAMERA;
    public static final String[] CONTACTS;
    public static final String[] LOCATION;
    public static final String[] MICROPHONE;
    public static final String[] PHONE;
    public static final String[] SENSORS;
    public static final String[] SMS;
    public static final String[] STORAGE;

    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            CALENDAR = new String[]{};
            CAMERA = new String[]{};
            CONTACTS = new String[]{};
            LOCATION = new String[]{};
            MICROPHONE = new String[]{};
            PHONE = new String[]{};
            SENSORS = new String[]{};
            SMS = new String[]{};
            STORAGE = new String[]{};
        } else {
            CALENDAR = new String[]{
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR};

            CAMERA = new String[]{
                    Manifest.permission.CAMERA};

            CONTACTS = new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.GET_ACCOUNTS};

            LOCATION = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};

            MICROPHONE = new String[]{
                    Manifest.permission.RECORD_AUDIO};

            PHONE = new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.WRITE_CALL_LOG,
                    Manifest.permission.USE_SIP,
                    Manifest.permission.PROCESS_OUTGOING_CALLS};

            SENSORS = new String[]{
                    Manifest.permission.BODY_SENSORS};

            SMS = new String[]{
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_WAP_PUSH,
                    Manifest.permission.RECEIVE_MMS};

            STORAGE = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
    }

}
在Android M以前使用某权限是不需要用户授权的，只要在Manifest中注册即可，在Android M之后需要注册并申请用户授权，所以我们根据系统版本在Android M以前用一个空数组作为权限组，在Android M以后用真实数组权限。


5.接下来用我自己封装的一个权限框架给大家演示
动态申请拍照权限：


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


注解方法(当参数没有接口的时候，就会在当前类里面寻找相应的注解方法）：

 @ZbPermissionSuccess(requestCode = REQUEST_CONTACT)
public void permissionSuccessContact() {
    Toast.makeText(MainActivity.this, "成功授予Contact权限注解" , Toast.LENGTH_SHORT).show();
}

@ZbPermissionFail(requestCode = REQUEST_CONTACT)
public void permissionFailContact() {
    Toast.makeText(MainActivity.this, "授予Contact权限失败注解" , Toast.LENGTH_SHORT).show();
}

申请权限的回调方法:

 @Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    ZbPermission.onRequestPermissionsResult(MainActivity.this, requestCode, permissions, grantResults);
}



完整的demo代码：


public class MainActivity extends AppCompatActivity{

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
            //request()方法的参数可以有也可以没有，有且不为空，就会回调ZbPermissionCallback的响应的回调方法，没有或为空，则
            //回调响应的注解方法
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
            // 没有callback作为参数 就会去调用响应的注解方法
                ZbPermission.needPermission(MainActivity.this, REQUEST_STORAGE, Permission.STORAGE);
            }
        });

        bt_request_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             // 有callback作为参数，若callback不为空，就会去调用响应的callback方法，否则就会去调用响应的注解方法
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

6.如果觉得这里描述的不够详细，可以查看demo，也可以参考我的博客
 http://blog.csdn.net/sinat_30472685/article/details/78071494
 
 谢谢观看，希望可以帮助到你，也感谢其他程序猿/媛的分享！
