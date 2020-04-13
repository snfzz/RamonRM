package com.ramon.ramonrm.SaiBangTJ.LiuFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ramon.ramonrm.R;
import com.ramon.ramonrm.SaiBangTJ.SaiBangTJBarChartActivity;

public class SBTJChartLiuFragment extends Fragment {
    private int num=1;
    int [] a={3,1,2};
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //不同的Activity对应不同的布局
        View view = inflater.inflate(R.layout.fragment_sbtjchartliu, container, false);
        return view;
    }

    public SBTJChartLiuFragment(int a){
        num=a;
        Log.e("test2",a+"");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /**
         * TODO 实现底部菜单对应布局控件事件
         * */
        Button btn=getActivity().findViewById(R.id.btn);


        for (int i=1;i<=3;i++){
            if (num==a[i-1]){
                btn.setText(num+"");
            }
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),getClass().getSimpleName(),Toast.LENGTH_LONG).show();
            }
        });
    }

}
