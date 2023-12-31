package com.example.studentapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.example.studentapp.adapters.PlanAddRecycler;
import com.example.studentapp.al.PlanToDay;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.databinding.FragmentSettingPlanBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Plan;
import com.example.studentapp.db.Questions;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;

import java.util.ArrayList;
import java.util.stream.Collectors;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingPlanFragment extends Fragment {
    private PlanAddRecycler adapter;
    private FragmentSettingPlanBinding binding;
    private ApiInterface apiInterface;
    private StatisticFragmentArgs args;
    private PlanToSub subj;
    private int ind = 1;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setPlan();
        binding.AddPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean p [] = adapter.getPlanArray();

                    ArrayList<PlanToDay> plans = new ArrayList<>();

                    for (int i = 0; i < subj.getFuturePlan().size(); i++)
                        if (p[i]) plans.add(subj.getFuturePlan().get(i));
                if(plans.isEmpty()) {
                    Toast.makeText(getContext(), "Укажите план подготовки", Toast.LENGTH_SHORT).show();
                } else {
                    subj.setFuture(plans);
                    MainActivity.myDBManager.updatePlan(subj);
                    Users.getUser().currentUpdateDbTime();

                    NavDirections action = SettingPlanFragmentDirections.actionSettingPlanFragmentToListFragment2();
                    Navigation.findNavController(getView()).navigate(action);
                }
            }
        });

        binding.all1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ind = ind == 0 ? 1 : 0;
                editPlan(subj.getFuturePlan(),ind);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_setting_plan, container, false);
        Paper.init(getContext());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        args = StatisticFragmentArgs.fromBundle(getArguments());
        return binding.getRoot();
    }

    private void setPlan(){
        subj = MainActivity.myDBManager.getFromDB().stream()
                .filter( c -> c.getSub().getNameOfSubme().equals(args.getId()) ).collect(Collectors.toList()).get(0);

        binding.listPlan.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listPlan.setHasFixedSize(true);
        adapter = new PlanAddRecycler(getActivity(),subj.getFuturePlan(),1);
        binding.listPlan.setAdapter(adapter);


       /* Call<Subjects> getSubj = apiInterface.getSubjectById(args.getId());
        getSubj.enqueue(new Callback<Subjects>() {
            @Override
            public void onResponse(Call<Subjects> call, Response<Subjects> response) {
                if (response.body()!= null){
                    subj = new Subjects(response.body());
                    binding.listPlan.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.listPlan.setHasFixedSize(true);
                    adapter = new PlanAddRecycler(getActivity(),subj.getPlans(),1);
                    binding.listPlan.setAdapter(adapter);
                }else{
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Subjects> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/
    }
    private  void editPlan(ArrayList<PlanToDay> p , int check){
        binding.listPlan.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listPlan.setHasFixedSize(true);
        adapter = new PlanAddRecycler(getActivity(),p,check);
        binding.listPlan.setAdapter(adapter);

    }


}