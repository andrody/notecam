package Adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import camera.CustomCameraHost;
import helper.Singleton;
import view_fragment.CameraFragment;
import view_fragment.MateriasFragment;
import view_fragment.TopicosFragment;
import welcome_fragments.WelcomeFragment_1;
import welcome_fragments.WelcomeFragment_2;
import welcome_fragments.WelcomeFragment_3;

public class PagerAdapter extends FragmentPagerAdapter {

    Context context;
    public Fragment mFragmentAtPos0 = null;
    public Fragment mFragmentAtPos1 = null;
    public Fragment mFragmentAtPos2 = null;
    final private FragmentManager mFragmentManager;

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mFragmentManager = fm;
        this.context = context;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
        //return POSITION_NONE;
    }

    private final String TAG_CAMERA_FRAGMENT = "camera_fragment";

    @Override
    public Fragment getItem(int position) {

        //Se houver matérias criadas vai para a tela normal
        if(!Singleton.getMateriasActivity().isEmptyFragments()) {

            if (position == 0) {
                if(mFragmentAtPos0 == null) {
                    mFragmentAtPos0 = new MateriasFragment();
                    Singleton.setMateriasFragment((MateriasFragment) mFragmentAtPos0);
                }
                return mFragmentAtPos0;


            } else if (position == 1) {

                CameraFragment c = null;
                if (mFragmentAtPos1 == null) {
                    c = new CameraFragment(); //CameraFragment.newInstance(false);
                    c.setHost(new CustomCameraHost(context));
                    mFragmentAtPos1 = c;
                }
                Singleton.setCameraFragment((CameraFragment) mFragmentAtPos1);
                return mFragmentAtPos1;


            } else if (position == 2) {

                if (mFragmentAtPos2 == null) {
                    mFragmentAtPos2 = TopicosFragment.newInstance(Singleton.getMateria_selecionada().getId());
                }
                Singleton.setTopicosFragment((TopicosFragment) mFragmentAtPos2);
                return mFragmentAtPos2;
            }

            return mFragmentAtPos0;

        }

        //Se não, vai para a tela de boas vindas
        else
            return getWelcomeItem(position);
    }

    public Fragment getWelcomeItem(int position) {


        if (position == 0) {
            if(mFragmentAtPos0 == null) {
                mFragmentAtPos0 = new WelcomeFragment_1();
            }
            return mFragmentAtPos0;


        } else if (position == 1) {

            if(mFragmentAtPos1 == null) {
                mFragmentAtPos1 = new WelcomeFragment_2();
            }
            return mFragmentAtPos1;

        } else if (position == 2) {

            if(mFragmentAtPos2 == null) {
                mFragmentAtPos2 = new WelcomeFragment_3();
            }
            return mFragmentAtPos2;

        }

        return mFragmentAtPos0;

    }

    @Override
    public int getCount() {
        return 3;
    }

    public FragmentManager getmFragmentManager() {
        return mFragmentManager;
    }
}