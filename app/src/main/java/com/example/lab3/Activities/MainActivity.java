package com.example.lab3.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.example.lab3.Fragments.AccountFragment;
import com.example.lab3.Fragments.StatisticFragment;
import com.example.lab3.Fragments.RoomFragment;
import com.example.lab3.R;
import com.example.lab3.ViewModels.AccountViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(new StatisticFragment());
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.dashboardFragment:
                fragment = new StatisticFragment();
                setTitle(getString(R.string.title_statistic));
                break;
            case R.id.roomFragment:
                fragment = new RoomFragment();
                setTitle(getString(R.string.title_game));
                break;
            case R.id.accountFragment:
                fragment = new AccountFragment();
                setTitle(getString(R.string.title_account));
                break;
        }
        loadFragment(fragment);
        return true;
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}