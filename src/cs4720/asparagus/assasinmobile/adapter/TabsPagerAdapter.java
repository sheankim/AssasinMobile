package cs4720.asparagus.assasinmobile.adapter;

import cs4720.asparagus.assasinmobile.MyGamesFragment;
import cs4720.asparagus.assasinmobile.CreateGamesFragment;
import cs4720.asparagus.assasinmobile.AdminGamesFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter{

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int index) {
		// TODO Auto-generated method stub
		switch (index){
		case 0:
			return new MyGamesFragment();
		case 1:
			return new CreateGamesFragment();
		case 2:
			return new AdminGamesFragment();
		}
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}

}
