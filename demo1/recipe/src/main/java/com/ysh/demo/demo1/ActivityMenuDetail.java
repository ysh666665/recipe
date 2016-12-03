package com.ysh.demo.demo1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ysh.demo.demo1.constant.Contant;
import com.ysh.demo.demo1.gsonbean.SingleMenu;
import com.ysh.demo.demo1.gsonbean.StepBean;
import com.ysh.demo.demo1.litepalbean.RecipeItemBean;
import com.ysh.demo.demo1.retrofit.ApiException;
import com.ysh.demo.demo1.retrofit.HttpUtils;
import com.ysh.demo.demo1.utils.DimensionConvertUtil;
import com.ysh.demo.demo1.utils.NetworkJugde;
import com.ysh.demo.demo1.utils.Toastutil;

import org.litepal.crud.DataSupport;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.ysh.demo.demo1.ActivitySearchMenu.SEARCH_CONTENT;


public class ActivityMenuDetail extends AppCompatActivity {

    private CheckBox favor;
    private ImageView ivRecipeImage;
    private TextView tvRecipeTitle;
    private TextView tvRecipeSummary;
    private LinearLayout llIngredients;
    private LinearLayout llMethod;
    private LinearLayout llTitle;
    private static final String TAG = "ActivityMenuDetail";
    private TextView tvIngredientDetails;
    private Gson gson = new Gson();
    private DimensionConvertUtil dimConvertor;
    private int padding;
    private int textSp;
    private SingleMenu mSingleMenu;
    private String mSingleMenuId;
    private boolean hasSaved = false;
//    private final String colorCode_blue = "#a9b7b7";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);
        dimConvertor = new DimensionConvertUtil(this);
        padding = (int) dimConvertor.convertToDp(12);
        textSp = (int) dimConvertor.convertToSp(6);
        Log.d(TAG, "onCreate:textSp: " + textSp + "  padding:" + padding);
        initViews();
        getRecipeId();
        if (getCurrentRecipeItemBean(mSingleMenuId) != null) {
            hasSaved = true;
            favor.setChecked(true);
        }

        setViewData();
    }

    @Override
    public void onBackPressed() {
        if (favor.isChecked() && !hasSaved) {
            saveData(mSingleMenu);
        } else if (mSingleMenuId != null && !favor.isChecked()) {
            deleteData(mSingleMenuId);
            setActivityResults(RESULT_OK);
            Log.d(TAG, "onDestroy: "+favor.isChecked());
        }
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void setActivityResults(int resultCode) {
        Intent intent = new Intent();
        intent.putExtra(Contant.KEY_FOR_RESULT, favor.isChecked());
        setResult(resultCode,intent);
    }

    public static void actionStartActivity(Context activity, @NonNull String s) {

        activity.startActivity(getIntent(activity, s));
    }

    @NonNull
    private static Intent getIntent(Context activity, @NonNull String s) {
        Intent intent = new Intent(activity, ActivityMenuDetail.class);
        intent.putExtra(SEARCH_CONTENT, s);
        return intent;
    }

    public static void actionStartActivityForResult(Activity activity, @NonNull String s, @NonNull int requestCode) {
        activity.startActivityForResult(getIntent(activity, s), requestCode);
    }


    private void getRecipeId() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mSingleMenuId = bundle.getString(SEARCH_CONTENT);
            Log.d(TAG, "getRecipeId: " + mSingleMenuId);
        }
    }

    private void initViews() {

        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        EditText etShallGone = (EditText) findViewById(R.id.editText);
        etShallGone.setVisibility(View.INVISIBLE);
        TextView tvCollect = (TextView) findViewById(R.id.btnSearch);
        tvCollect.setText("收藏");
        tvCollect.setTextSize(16);
        tvCollect.setClickable(false);
        favor = (CheckBox) findViewById(R.id.cbxFavor);
        favor.setVisibility(View.VISIBLE);
//        favor.setChecked(true);

        ivRecipeImage = (ImageView) findViewById(R.id.ivRecipeImage);
        tvRecipeTitle = (TextView) findViewById(R.id.tvRecipeTitle);
        tvRecipeSummary = (TextView) findViewById(R.id.tvRecipeSummary);
        llIngredients = (LinearLayout) findViewById(R.id.llIngredients);
        llMethod = (LinearLayout) findViewById(R.id.llMethod);
        llTitle = (LinearLayout) findViewById(R.id.search_layout);
//        llTitle.setBackgroundColor(Color.parseColor(colorCode_blue));
        tvIngredientDetails = (TextView) findViewById(R.id.ingredientDetails);
    }

    private void setViewData() {
        if (mSingleMenuId != null && NetworkJugde.getInstance().isNetworkConnected(this)) {
            Observable<SingleMenu> call = (Observable<SingleMenu>) HttpUtils.getInstance().getCall(HttpUtils.SEARCH_BY_MENU_ID, mSingleMenuId, 0);
            call.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<SingleMenu, SingleMenu>() {
                        @Override
                        public SingleMenu call(SingleMenu singleMenu) {
                            int retCode = singleMenu.getRetCode();
                            if (retCode != 200) {
                                throw new ApiException(retCode);
                            }
                            return singleMenu;
                        }
                    })
                    .subscribe(new Subscriber<SingleMenu>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Toastutil.showToast(ActivityMenuDetail.this, "获取数据失败");
                        }

                        @Override
                        public void onNext(SingleMenu singleMenu) {
                            SingleMenu.ResultBean.RecipeBean recipe = singleMenu.getResult().getRecipe();
                            if (recipe != null) {
                                String recipeImageUri = recipe.getImg();//图片地址
                                String recipeTitle = singleMenu.getResult().getName();//菜名
                                String sumary = recipe.getSumary();//概要
                                String method = recipe.getMethod();//方法流程
                                String ingredients = recipe.getIngredients();//用料
                                Log.d(TAG, "onNext: method:" + method);
                                mSingleMenu = singleMenu;
                                List<StepBean> stepList = gson.fromJson(method, new TypeToken<List<StepBean>>() {
                                }.getType());//步骤列表

                                Log.d(TAG, "onNext: " + 2);
                                if (ingredients != null) {
                                    String temps = ingredients.substring(1, ingredients.length() - 1);
                                    tvIngredientDetails.setText(temps);//设置材料
                                    Log.d(TAG, "111111111111111111111111 " + temps);
                                } else {
                                    llIngredients.setVisibility(View.GONE);
                                }

                                //部署数据
                                Log.d(TAG, "onNext: recipeImageUri" + recipeImageUri);
                                if (recipeImageUri != null) {
                                    Glide.with(ActivityMenuDetail.this).load(recipeImageUri).into(ivRecipeImage);//设置图片
                                } else {
                                    ivRecipeImage.setVisibility(View.GONE);
                                }

                                tvRecipeTitle.setText(recipeTitle);//设置菜名

                                if (sumary != null) {
                                    tvRecipeSummary.setText(sumary);//设置概要
                                } else {
                                    tvRecipeSummary.setVisibility(View.GONE);
                                }
                                if (stepList != null) {
                                    for (StepBean step : stepList) {
                                        Log.d(TAG, "onNext: step" + step.getStep());
                                        TextView tv = new TextView(ActivityMenuDetail.this);
                                        ImageView iv = new ImageView(ActivityMenuDetail.this);
                                        tv.setPadding(padding, padding, padding, padding);
                                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                        tv.setText(step.getStep());
                                        llMethod.addView(tv);
                                        iv.setAdjustViewBounds(true);
//                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(iv.getLayoutParams());
//                                    params.setMargins(32,32,32,32);
//                                    iv.setLayoutParams(params);
//                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                                        iv.setPadding(100, 0, 100, 0);
//                                Log.d(TAG, "onNext: uri："+step.getImg());
                                        llMethod.addView(iv);
                                        Glide.with(ActivityMenuDetail.this).load(step.getImg()).into(iv);
                                    }
                                } else {
                                    llMethod.setVisibility(View.GONE);
                                }
                            }


                        }
                    });
        }

    }

    private boolean saveData(SingleMenu singleMenu) {
        /*
        * 储存到数据库包括
        * 略缩图片uri
        * 菜名
        * 描述
        * 菜谱id
        * */
        SingleMenu.ResultBean result = singleMenu.getResult();
        SingleMenu.ResultBean.RecipeBean recipe;
        if (result != null) {
            String imageUri = result.getThumbnail();
            String name = result.getName();
            recipe = result.getRecipe();
            String desc = "";
            if (recipe != null) {
                desc = recipe.getSumary();
                if (desc == null) {
                    desc = recipe.getTitle();
                }
            }
            String id = result.getMenuId();
            RecipeItemBean data = new RecipeItemBean(imageUri, name, desc, id);
            return data.save();
        }
        return false;
    }

    private void deleteData(String menuId) {
        DataSupport.deleteAll(RecipeItemBean.class, "menuId = ?", menuId);
    }

    private RecipeItemBean getCurrentRecipeItemBean(String menuId) {
        List<RecipeItemBean> tempData = DataSupport.where("menuId = ?", menuId).find(RecipeItemBean.class);
        if (tempData != null && tempData.size() > 0) {
            return tempData.get(0);
        }
        return null;
    }

}
