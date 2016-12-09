package com.igormelo.rxadapterrecyclerexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import com.igormelo.rxadapterrecyclerexample.databinding.ActivityMainBinding;
import com.igormelo.rxadapterrecyclerexample.databinding.ItemLayoutBinding;
import com.minimize.android.rxrecycleradapter.OnGetItemViewType;
import com.minimize.android.rxrecycleradapter.RxDataSource;
import com.minimize.android.rxrecycleradapter.TypesViewHolder;
import com.minimize.android.rxrecycleradapter.ViewHolderInfo;
import java.util.ArrayList;
import java.util.List;
import rx.functions.Action1;
import rx.functions.Func1;
public class MainActivity extends AppCompatActivity {

    List<String> dataSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        //ViewHolderInfo
        List<ViewHolderInfo>viewHolderInfoList = new ArrayList<>();
        viewHolderInfoList.add(new ViewHolderInfo(R.layout.item_layout, 1));
        viewHolderInfoList.add(new ViewHolderInfo(R.layout.item_header_layout, 0));

        dataSet = new ArrayList<>();
        dataSet.add("this");
        dataSet.add("oi");

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RxDataSource<String> rxDataSource = new RxDataSource<>(dataSet);
        rxDataSource.map(String::toUpperCase)
                .repeat(10)
                .<ItemLayoutBinding>bindRecyclerView(binding.recyclerView,R.layout.item_layout).subscribe(viewHolder ->{
            ItemLayoutBinding b = viewHolder.getViewDataBinding();
            String item = viewHolder.getItem();
            b.textViewItem.setText(String.valueOf(item));
        });
        dataSet = rxDataSource.getRxAdapter().getDataSet();

    }
}
