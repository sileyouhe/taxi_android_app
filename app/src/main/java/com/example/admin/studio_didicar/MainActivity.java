package com.example.admin.studio_didicar;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.admin.studio_didicar.Navi.DrivingRouteOverlay;
import com.example.admin.studio_didicar.Navi.OverlayManager;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.push.PushConstants;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.InstallationListener;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private MapView mMapView = null;
    private LocationClient mLocationClient;
    private BDLocationListener mLocationListener;
    private boolean isFirstin = true;
    private BaiduMap mBaiduMap;
    private TextView callNum;
    private Button driver, passenager, retu, callCar, navi, pay,account;
    private boolean isLogin = false, isCallCar = false;
    private double mlatitude, mlongitude;
    private String nameStart = "乘客/司机:账户";
    int num = 0;
    private EditText fromText, toText;
    private BroadcastReceiver mBroadReceiver;
    StringBuffer str_middle = new StringBuffer();
    StringBuffer str_nickcall = new StringBuffer();
    StringBuffer str_name = new StringBuffer();
    private PlanNode node_middle;
    private double money = 0;
    private double distance_middle;
    private StringBuffer str_middle_passId = new StringBuffer();
    private StringBuffer str_middle_driId = new StringBuffer();
    private StringBuffer str_middle_passMoney = new StringBuffer();
    private StringBuffer str_middle_objectId=new StringBuffer();
    private StringBuffer str_middle_nickCall=new StringBuffer();
    private StringBuilder str_middle_tolatitude=new StringBuilder();
    private StringBuilder str_middle_tolongitude=new StringBuilder();


    //移植进来的功能：：
    //新加入的点：
    private LatLng myLastLocation;//每五秒一次定位的数据保存在这里
    private LatLng myDesLocation; //长按后的点的数据保存在这里

    //司机，乘客
    private LatLng DriverLocation;
    private LatLng FromLocation;
    private LatLng ToLocation;


    //路线规划：
    PlanNode stNode;   //开始点
    PlanNode enNode;    //终点

    PlanNode DriverNode;
    PlanNode FromNode;
    PlanNode ToNode;

    RoutePlanSearch mSearch;
    OverlayManager mrouteOverlay = null;
    Button btnRoute;
    int distence;   //路径规划中起点和终点的距离
    int distanceMoney;
    //Toast语句在下面一个叫onGetDrivingRouteResult的里面，距离也是那个类计算出来的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        Bmob.initialize(this, "8d9c244e1e2f48e5f9a75c4e00966515");
        BmobInstallationManager.getInstance().initialize(new InstallationListener<BmobInstallation>() {
            @Override
            public void done(BmobInstallation bmobInstallation, BmobException e) {
                if (e == null) {
                    Logger.i(bmobInstallation.getObjectId() + "-" + bmobInstallation.getInstallationId());
                } else {
                    Logger.e(e.getMessage());
                }
            }
        });
        BmobPush.startWork(this);// 启动推送服务

        //test();

        initView();
        initLocation();
        initRoutePlan(); //路径规划  相关的一些控件和类的初始化
        receiver_data();

        //加入功能1：长按选点功能
        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //String info = "纬度是："+latLng.latitude+","+"经度是："+latLng.longitude;
                //Toast.makeText(MainActivity.this,info,Toast.LENGTH_LONG).show();
                str_middle_tolatitude.delete(0,str_middle_tolatitude.length());
                str_middle_tolongitude.delete(0,str_middle_tolongitude.length());
                str_middle_tolatitude.append(latLng.latitude);
                str_middle_tolongitude.append(latLng.longitude);

                myDesLocation = latLng;
                //在地图上出现一个点
                addDesOverlay(latLng);
                //经纬转中文地址
                jwToadd(latLng);

                stNode = PlanNode.withLocation(myLastLocation);
                enNode = PlanNode.withLocation(myDesLocation);
                mSearch.drivingSearch(new DrivingRoutePlanOption().from(stNode).to(enNode));







            }
        });
    }

    private void addDesOverlay(LatLng Deslat) {
        mBaiduMap.clear();
        OverlayOptions options = new MarkerOptions().position(Deslat)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark));
        mBaiduMap.addOverlay(options);


    }

    private void addOverlayNoclear(LatLng lat,int photo)
    {
        OverlayOptions options = new MarkerOptions().position(lat)
                .icon(BitmapDescriptorFactory.fromResource(photo));
        mBaiduMap.addOverlay(options);
    }

    private void jwToadd(LatLng ll) {
        GeoCoder Coder = GeoCoder.newInstance();
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            // 反地理编码查询结果回调函数
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null
                        || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                    Toast.makeText(MainActivity.this, "抱歉，未能找到结果",
                            Toast.LENGTH_LONG).show();
                }
                //Toast.makeText(MainActivity.this,
                //"位置：" + result.getAddress(), Toast.LENGTH_LONG)
                // .show();
                toText.setText(result.getAddress());
            }

            // 地理编码查询结果回调函数
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null
                        || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                }
            }
        };
        // 设置地理编码检索监听者
        Coder.setOnGetGeoCodeResultListener(listener);
        //传经纬度，发起地址检索
        Coder.reverseGeoCode(new ReverseGeoCodeOption().location(ll));

        //传地址，发起经纬度检索:Coder.geocode(new GeoCodeOption().city().address());
        Coder.destroy();
    }

    private void receiver_data() {
        mBroadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
                    String str = intent.getStringExtra("msg");
                    String strEnd = str.substring(13, str.length() - 2);
                    Log.i("tag", "strEnd"+strEnd);

                    String[] str_midddle_arr=new String[20];
                    str_midddle_arr=strEnd.split(" ");

                    for(int i=0;i<6;i++){
                        Log.i("tag",str_midddle_arr[i]);
                    }
                    String name = str_midddle_arr[0];
                    String from = str_midddle_arr[1];
                    String to = str_midddle_arr[2];

                    FromLocation=new LatLng(Double.parseDouble(str_midddle_arr[3]),Double.parseDouble(str_midddle_arr[4]));
                    ToLocation=new LatLng(Double.parseDouble(str_midddle_arr[5]),Double.parseDouble(str_midddle_arr[6]));
                    DriverLocation= myLastLocation;
                    mBaiduMap.clear();
                    addOverlayNoclear(FromLocation,R.drawable.icon_openmap_focuse_mark);
                    addOverlayNoclear(ToLocation,R.drawable.icon_openmap_mark);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("您收到一条来自乘客的订单");
                    builder.setMessage("电  话:" + name+ "\n" +
                            "出发地:" + from + "\n" + "目的地:" + to+"\n收益:"+str_midddle_arr[7]);
                    builder.show();
                }
            }
        };
    }

    private void test() {
        Passenger_table pass = new Passenger_table();
        pass.setNickCall("asfd");
        pass.setName("afsdadf");
        pass.setKey("asdf");
        pass.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {

            }
        });
        Driver_table dri = new Driver_table();
        dri.setNickCall("asfd");
        dri.setName("afsdadf");
        dri.setKey("asdf");
        dri.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {

            }
        });
    }


    private void initView() {
        driver = findViewById(R.id.driver);
        callNum = findViewById(R.id.callNum);
        passenager = findViewById(R.id.passenger);
        retu = findViewById(R.id.retu);
        fromText = findViewById(R.id.fromText);
        toText = findViewById(R.id.toText);
        callCar = findViewById(R.id.callCar);
        pay = findViewById(R.id.pay);
        navi = findViewById(R.id.BtnNavi);
        account=findViewById(R.id.account);

        account.setOnClickListener(this);
        pay.setOnClickListener(this);
        navi.setOnClickListener(this);
        retu.setOnClickListener(this);
        passenager.setOnClickListener(this);
        driver.setOnClickListener(this);
        mMapView = findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        callCar.setOnClickListener(this);

        mBaiduMap.setMyLocationEnabled(true);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
    }

    private void initLocation() {
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }


    private void initRoutePlan() {
        btnRoute = findViewById(R.id.btnRoute);
        btnRoute.setOnClickListener(this);
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(listener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.passenger:
                Intent intent1 = new Intent(MainActivity.this, Passenger.class);
                Log.i("tag", "passenager");
                startActivityForResult(intent1, 1);
                break;
            case R.id.driver:
                Intent intent2 = new Intent(MainActivity.this, driver.class);
                intent2.putExtra("latitude", String.valueOf(mlatitude));
                intent2.putExtra("longitude", String.valueOf(mlongitude));
                intent2.putExtra("installation", BmobInstallationManager.getInstallationId());
                startActivityForResult(intent2, 2);
                break;
            case R.id.retu:
                retu_site();
                break;
            case R.id.callCar:
                if (num == 1) {
                    callCarMethod1();
                    isCallCar = true;
                    pay.setVisibility(View.VISIBLE);
                } else
                    Toast.makeText(MainActivity.this, "您的乘客账号未登录成功，请登录再试", Toast.LENGTH_SHORT).show();
                break;
            case R.id.BtnNavi: {
                Intent i = new Intent(MainActivity.this, NaviActivity.class);
//                i.putExtra("lastlong", myLastLocation.longitude);
//                i.putExtra("lastlati", myLastLocation.latitude);
//                i.putExtra("deslong", myDesLocation.longitude);
//                i.putExtra("deslati", myDesLocation.latitude);

                i.putExtra("driverlong", DriverLocation.longitude);
                i.putExtra("driverlati", DriverLocation.latitude);
                i.putExtra("fromlong", FromLocation.longitude);
                i.putExtra("fromlati", FromLocation.latitude);
                i.putExtra("tolong", ToLocation.longitude);
                i.putExtra("tolati", ToLocation.latitude);
                startActivity(i);
                break;
            }
            case R.id.btnRoute: {
                //List<PlanNode> list = new ArrayList<PlanNode>();
               // DriverNode = PlanNode.withLocation(DriverLocation);
                //list.add(stNode);
                stNode = PlanNode.withLocation(myLastLocation);
                enNode = PlanNode.withLocation(myDesLocation);
                mSearch.drivingSearch(new DrivingRoutePlanOption().from(stNode).to(enNode));
               // mSearch.drivingSearch(new DrivingRoutePlanOption().from(DriverNode).passBy(list).to(enNode));
                break;
            }
            case R.id.pay: {
                payMethod();
                pay.setVisibility(View.GONE);
                break;
            }
            case  R.id.account:
                if(num==1||num==2){
                    Intent intent3 = new Intent(MainActivity.this, account.class);
                    intent3.putExtra("num",String.valueOf(num));
                    intent3.putExtra("objectId",str_middle_objectId.toString());
                    intent3.putExtra("nickCall",str_middle_nickCall.toString());
                    Log.i("tag", "passenager");
                    startActivityForResult(intent3, 3);
                }
                else{
                    Toast.makeText(MainActivity.this,"请先登录账号",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void payMethod() {
        BmobQuery<Driver_table> bmobQuery = new BmobQuery<Driver_table>();
        bmobQuery.addWhereEqualTo("objectId", str_middle_driId);
        bmobQuery.findObjects(new FindListener<Driver_table>() {
            @Override
            public void done(List<Driver_table> Driver_tables, BmobException e) {
                // TODO 自动生成的方法存根
                if (Driver_tables.size() > 0) {
                    Driver_table dri = new Driver_table();
                    dri.setWallet(Driver_tables.get(0).getWallet() + money);
                    dri.update(Driver_tables.get(0).getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.i("tag", "修改成功");
                            } else Log.i("tag", "修改失败");
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "司机信息查询失败", Toast.LENGTH_SHORT).show();
                }
            }
        });//修改司机钱包


        final Passenger_table dri = new Passenger_table();
        final double dou1=Double.parseDouble(str_middle_passMoney.toString())- money;
        str_middle_passMoney.delete(0,str_middle_passMoney.length());
        str_middle_passMoney.append(String.valueOf(dou1));
        dri.setWallet(dou1);
        dri.update(str_middle_passId.toString(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.i("tag", "修改成功");
                } else Log.i("tag", "修改失败");
                String callNumMiddle=callNum.getText().toString();
            }
        });//修改乘客钱包
    }


    private void callCarMethod1() {
        Log.i("tag", "开始叫车1");
        str_middle.delete(0, str_middle.length());
        BmobQuery<Driver_table> queryFirst = BmobInstallation.getQuery();
        if (distence < 7) money = 7;
        else money = (7 + (distence - 7) * 0.5)/100;

        queryFirst.findObjects(new FindListener<Driver_table>() {

            double dou = Double.MAX_VALUE;

            @Override
            public void done(List<Driver_table> list, BmobException e) {
                Log.i("tag_司机数量", String.valueOf(list.size()));
                for (Driver_table dri : list) {
                    double mmlatitude = Double.parseDouble(dri.getMlatitude());
                    double mmlongitude = Double.parseDouble(dri.getMlongitude());
                    LatLng lat = new LatLng(mmlatitude, mmlongitude);
                    node_middle = PlanNode.withLocation(lat);
                    stNode = PlanNode.withLocation(myLastLocation);
                    mSearch.drivingSearch(new DrivingRoutePlanOption().from(stNode).to(node_middle));
                    Log.i("tag", String.valueOf(distence));
                    if (distence < dou) {
                        dou = distence;
                        str_middle.delete(0, str_middle.length());
                        str_nickcall.delete(0, str_nickcall.length());
                        str_name.delete(0, str_name.length());
                        str_middle_driId.delete(0, str_middle_driId.length());
                        str_middle_driId.append(dri.getObjectId());
                        str_name.append(dri.getName());
                        str_middle.append(dri.getInstallationID());
                        str_nickcall.append(dri.getNickCall());
                    }
                    Log.i("tag", "dri.getInstallationID()=" + dri.getInstallationID());
                }
                callCarMethod2();
            }
        });
        Log.i("tag", "找到最近司机位置ID：" + str_middle.toString());
    }

    private void callCarMethod2() {
        Log.i("tag", "开始叫车2");
        if (str_middle.toString().equals("")) {
            Toast.makeText(MainActivity.this, "还没司机注册此软件，请耐心等待", Toast.LENGTH_SHORT).show();
            Log.i("tag", "还没司机注册此软件，请耐心等待");
        } else {
            BmobPushManager bmobPushManager = new BmobPushManager();
            BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
            query.addWhereEqualTo("installationId", str_middle.toString());
            bmobPushManager.setQuery(query);
            Log.i("tag",String.valueOf(myLastLocation.latitude)+" "+String.valueOf(myLastLocation.longitude)+" "+
                    String.valueOf(str_middle_tolatitude)+" "+String.valueOf(str_middle_tolongitude));
            String str = callNum.getText() + " " + fromText.getText() + " " + toText.getText()+" "+
                    String.valueOf(myLastLocation.latitude)+" "+String.valueOf(myLastLocation.longitude)+" "+
                    String.valueOf(str_middle_tolatitude)+" "+String.valueOf(str_middle_tolongitude)+
                    " "+money;
            bmobPushManager.pushMessage(str, new PushListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Logger.e("推送成功！");
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("司机" + str_name.substring(0, 1) + "师傅已结单");
                        builder.setMessage("需花费:"+money+"\n联系电话：" + str_nickcall.toString());
                        builder.show();
                    } else {
                        Logger.e("异常：" + e.getMessage());
                    }
                }
            });
        }
    }


    private void retu_site() {
        LatLng latLbg = new LatLng(mlatitude, mlongitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLbg);
        mBaiduMap.setMapStatus(msu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            if (data.getStringExtra("Is").equals("true")) {
                callNum.setText("乘客:" + data.getStringExtra("nickCall"));
                str_middle_nickCall.delete(0,str_middle_nickCall.length());
                str_middle_nickCall.append(data.getStringExtra("nickCall"));
                str_middle_passId.delete(0, str_middle_passId.length());
                str_middle_passId.append(data.getStringExtra("objectId"));
                Log.i("tag","1");
                str_middle_objectId.delete(0, str_middle_objectId.length());
                Log.i("tag","2");
                str_middle_objectId.append(data.getStringExtra("objectId"));
                Log.i("tag","3");
                str_middle_passMoney.delete(0,str_middle_passMoney.length());
                str_middle_passMoney.append(data.getStringExtra("money"));
                isLogin = true;
                num = 1;
            } else {
                callNum.setText(nameStart);
                isLogin = false;
                num = 0;
            }
        } else if (requestCode == 2 && resultCode == 2) {
            if (data.getStringExtra("Is").equals("true")) {
                callNum.setText("司机:" + data.getStringExtra("nickCall"));
                str_middle_objectId.delete(0, str_middle_objectId.length());
                str_middle_objectId.append(data.getStringExtra("objectId"));
                isLogin = true;
                num = 2;
            } else {
                isLogin = false;
                callNum.setText(nameStart);
                num = 0;
            }
        }
    }

    //定位相关的监听器实现
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            MyLocationData data = new MyLocationData.Builder()
                    .accuracy(location.getRadius()).direction(100)
                    .latitude(location.getLatitude()).longitude(location.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(data);
            mlatitude = location.getLatitude();
            mlongitude = location.getLongitude();
            fromText.setText(location.getAddrStr());
            myLastLocation = new LatLng(mlatitude, mlongitude);

            if (isFirstin) {
                Log.i("tag", Double.toString(location.getLatitude()) + " " + Double.toString(location.getLongitude()));
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(latLng).zoom(15.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                isFirstin = false;
            }
        }
    }


    //路径规划相关的监听器实现
    OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                //result.getSuggestAddrInfo()
                //Toast.makeText(NaviActivity.this, result.getSuggestAddrInfo().toString(), Toast.LENGTH_SHORT).show();
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                mBaiduMap.clear();
                // mroute = result.getRouteLines().get(0);
                DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
                mrouteOverlay = overlay;
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
                distence = result.getRouteLines().get(0).getDistance();
                Toast.makeText(MainActivity.this, "起终点距离：" + distence, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }
    };

    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_focuse_mark);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i("tag", "start");

        IntentFilter dynamic_filter = new IntentFilter();
        dynamic_filter.addAction(PushConstants.ACTION_MESSAGE);            //添加动态广播的Action
        registerReceiver(mBroadReceiver, dynamic_filter);

        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted())
            mLocationClient.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}
