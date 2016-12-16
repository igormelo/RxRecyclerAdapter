package com.igormelo.rxadapterrecyclerexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.LinearLayoutManager;

import com.igormelo.rxadapterrecyclerexample.databinding.ActivityMainBinding;
import com.igormelo.rxadapterrecyclerexample.databinding.ItemHeaderLayoutBinding;
import com.igormelo.rxadapterrecyclerexample.databinding.ItemLayoutBinding;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.minimize.android.rxrecycleradapter.OnGetItemViewType;
import com.minimize.android.rxrecycleradapter.RxDataSource;
import com.minimize.android.rxrecycleradapter.TypesViewHolder;
import com.minimize.android.rxrecycleradapter.ViewHolderInfo;
import java.util.ArrayList;
import java.util.List;
import rx.functions.Action1;
import rx.functions.Func1;
public class MainActivity extends AppCompatActivity {
    final int TYPE_HEADER = 0;
    final int TYPE_ITEM = 1;
    List<String> dataSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        //ViewHolderInfo
        List<ViewHolderInfo>viewHolderInfoList = new ArrayList<>();
        viewHolderInfoList.add(new ViewHolderInfo(R.layout.item_layout, TYPE_ITEM));
        viewHolderInfoList.add(new ViewHolderInfo(R.layout.item_header_layout, TYPE_HEADER));

        int i;
        for(i=0; i<10;i++){
            dataSet = new ArrayList<>(i);
            dataSet.add("oi: ");
            dataSet.add("como: ");
            dataSet.add("vai: ");
            dataSet.add("voce? ");

        }



        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RxDataSource<String> rxDataSource = new RxDataSource<>(dataSet);
        rxDataSource.map(String::toUpperCase)
                .repeat(10)
                .<ItemLayoutBinding>bindRecyclerView(binding.recyclerView,R.layout.item_layout).subscribe(viewHolder ->{
            ItemLayoutBinding b = viewHolder.getViewDataBinding();
            b.textViewItem.setText(viewHolder.getItem());
        });
        dataSet = rxDataSource.getRxAdapter().getDataSet();

        RxTextView.afterTextChangeEvents(binding.searchEditText).subscribe(new Action1<TextViewAfterTextChangeEvent>() {
            @Override
            public void call(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
                rxDataSource.updateDataSet(dataSet)
                        .filter(new Func1<String, Boolean>() {
                            @Override
                            public Boolean call(String s) {
                                return s.toLowerCase().contains(textViewAfterTextChangeEvent.view().getText());
                            }
                        }).updateAdapter();
            }
        });
            rxDataSource.bindRecyclerView(binding.recyclerView, viewHolderInfoList, new OnGetItemViewType() {
                @Override
                public int getItemViewType(int position) {
                    if (position % 2 == 0) {
                        return TYPE_HEADER;
                    }
                    return TYPE_ITEM;
                }
            }).subscribe(vH -> {
                final ViewDataBinding b = vH.getViewDataBinding();
                if(b instanceof ItemLayoutBinding){
                    final ItemLayoutBinding iB = (ItemLayoutBinding) b;
                    iB.textViewItem.setText("ITEM:" + vH.getItem());
                } else if (b instanceof ItemHeaderLayoutBinding) {
                    ItemHeaderLayoutBinding hB = (ItemHeaderLayoutBinding) b;
                    hB.textViewHeader.setText("HEADER: " + vH.getItem());
                }
            });
        rxDataSource.filter(s -> s.length() > 0).map(String::toUpperCase).updateAdapter();
    }
}
