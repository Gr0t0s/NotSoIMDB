package com.pestov.notsoimdb.controller;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.pestov.notsoimdb.R;
import com.pestov.notsoimdb.model.Film;

import java.util.ArrayList;

public class FragmentMain extends Fragment implements View.OnClickListener, View.OnFocusChangeListener
{
	
	private EditText etSearch;
	private Button btnSearch;
	private LinearLayout llFilmList;
	private ProgressBar pbFilmLoading;
	private String queryString;
	
	public FragmentMain()
	{
		// Required empty public constructor
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		etSearch = view.findViewById(R.id.etSearch);
		btnSearch = view.findViewById(R.id.btnSearch);
		llFilmList = view.findViewById(R.id.llFilmList);
		pbFilmLoading = view.findViewById(R.id.pbFilmLoading);
		etSearch.setOnClickListener(this);
		etSearch.setOnFocusChangeListener(this);
		btnSearch.setOnClickListener(this);
		queryString = "";
		return view;
	}
	
	public void fillEntries(ArrayList<Film> films)
	{
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		int id = 1;
		for (Film film : films)
		{
			//Adding new fragment container to the list
			FrameLayout frameLayout = new FrameLayout(getContext());
			frameLayout.setId(id);
			llFilmList.addView(frameLayout);
			//Replacing it with a new entry
			FragmentFilmEntry fragmentFilmEntry = new FragmentFilmEntry();
			fragmentFilmEntry.setFilm(film);
			ft.replace(id, fragmentFilmEntry);
			id++;
		}
		ft.commit();
		pbFilmLoading.setVisibility(View.GONE);
	}
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnSearch:
				queryString = etSearch.getText().toString();
				llFilmList.removeAllViews();
				pbFilmLoading.setVisibility(View.VISIBLE);
				etSearch.clearFocus();
				//Hide keyboard
				InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
				MainActivity mainActivity = (MainActivity) getActivity();
				mainActivity.getFilmsByTitle(this);
				break;
		}
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus)
	{
		switch (v.getId())
		{
			case R.id.etSearch:
				if (etSearch.isFocused())
				{
					etSearch.setText("");
					etSearch.setTextColor(getResources().getColor(R.color.black));
				}
				else
				{
					if (etSearch.getText().toString().equals(""))
						etSearch.setText(getResources().getText(R.string.search));
					etSearch.setTextColor(getResources().getColor(R.color.gray));
				}
				break;
		}
	}
	
	public String getQueryString()
	{
		return queryString;
	}
}
