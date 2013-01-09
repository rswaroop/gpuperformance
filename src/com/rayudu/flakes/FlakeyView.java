package com.rayudu.flakes;

import android.renderscript.RSSurfaceView;
import android.renderscript.RenderScriptGL;

import android.content.Context;
import android.view.SurfaceHolder;

public class FlakeyView  extends RSSurfaceView {

    private RenderScriptGL mRS;
    
    private Flakey mRender;

    public FlakeyView(Context context) {
        super(context);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        super.surfaceChanged(holder, format, w, h);
        if(mRS == null)
        {
        	RenderScriptGL.SurfaceConfig sc = new RenderScriptGL.SurfaceConfig();
        	mRS = createRenderScriptGL(sc);
        	mRS.setSurface(holder, w, h);
        	
        	mRender = new Flakey();
        	mRender.init(mRS, getResources(), w , h);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mRender = null;
        if (mRS != null) {
            mRS = null;
            destroyRenderScriptGL();
        }
    }

    void showFPS(boolean show){
    	mRender.showFPS(show);
    }
    
    void changeNumFlakes(boolean more){
    	mRender.changeNumFlakes(more);
    }    
}
