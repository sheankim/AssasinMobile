package cs4720.asparagus.assasinmobile.adapter;

import cs4720.asparagus.assasinmobile.GamePlayersFragment;
import cs4720.asparagus.assasinmobile.MyTargetFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class GameTabsPagerAdapter extends FragmentPagerAdapter{

	public GameTabsPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int index) {
		// TODO Auto-generated method stub
		switch (index){
		case 0:
			return new MyTargetFragment();
		case 1:
			return new GamePlayersFragment();
		}
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
	}

}
