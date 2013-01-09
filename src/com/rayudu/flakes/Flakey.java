package com.rayudu.flakes;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.*;

public class Flakey {
	
	private Resources mRes;
	private RenderScriptGL mRS;
	
	private ScriptField_Flake mFlakes;
	private ProgramVertex mProgVertex;
	private ProgramVertexFixedFunction.Constants mPVA;
	private ProgramFragment mProgFragment;
	private int numFlakes = 16;
	
	private ScriptC_flakey mScript;
	
	public void init(RenderScriptGL rs, Resources res, int width, int height){
		mRS = rs;
		mRes = res;
		
		mScript = new ScriptC_flakey(mRS,mRes,R.raw.flakey);
		
		mFlakes = new ScriptField_Flake(mRS, numFlakes, Allocation.USAGE_SCRIPT);
		mScript.bind_flakes(mFlakes);
		
		Bitmap flakeBitmap = BitmapFactory.decodeResource(mRes, R.drawable.flares);
		
		float flakeW = flakeBitmap.getWidth();
		float flakeH = flakeBitmap.getHeight();
		
		mScript.set_droidW(flakeBitmap.getWidth());
		mScript.set_droidH(flakeBitmap.getHeight());
		
		Mesh.TriangleMeshBuilder tmb = new Mesh.TriangleMeshBuilder(mRS, 2, Mesh.TriangleMeshBuilder.TEXTURE_0);
		
		tmb.setTexture(0,0);
		tmb.addVertex(0,0f);
		tmb.setTexture(1,0);
		tmb.addVertex(flakeW,0);
		tmb.setTexture(1,1);
		tmb.addVertex(flakeW,flakeH);
		tmb.setTexture(0,1);
		tmb.addVertex(0,flakeH);
		tmb.addTriangle(0,3,1);
		tmb.addTriangle(1,3,2);
		
		Mesh flakeMesh = tmb.create(true);
		mScript.set_flakeQuad(flakeMesh);
		
		ProgramVertexFixedFunction.Builder pvb = new ProgramVertexFixedFunction.Builder(mRS);
		mProgVertex = pvb.create();
		mPVA = new ProgramVertexFixedFunction.Constants(mRS);
		((ProgramVertexFixedFunction)mProgVertex).bindConstants(mPVA);
		Matrix4f proj = new Matrix4f();
		proj.loadOrthoWindow(mRS.getWidth(), mRS.getHeight());
		mPVA.setProjection(proj);
		mScript.set_gProgVertex(mProgVertex);
		
		ProgramFragmentFixedFunction.Builder txb = new ProgramFragmentFixedFunction.Builder(mRS);
		txb.setTexture(ProgramFragmentFixedFunction.Builder.EnvMode.DECAL, ProgramFragmentFixedFunction.Builder.Format.RGBA, 0);
		
		mProgFragment = txb.create();
		mProgFragment.bindTexture(loadTexture(R.drawable.flares), 0);
		mProgFragment.bindSampler(Sampler.CLAMP_LINEAR_MIP_LINEAR(mRS), 0);
		
		mScript.set_gProgFragment(mProgFragment);
		
		mRS.bindProgramStore(ProgramStore.BLEND_ALPHA_DEPTH_NONE(mRS));
		
		mScript.invoke_initScript(width,height,numFlakes);
		
		mRS.bindRootScript(mScript);

	}
		void showFPS(boolean show){
			mScript.set_drawFps(show);
		}
		
		void changeNumFlakes(boolean more){
			@SuppressWarnings("unused")
			int oldNumFlakes = numFlakes;
			numFlakes = more ? (numFlakes * 2) : (numFlakes / 2);
			numFlakes = Math.max(numFlakes,1);
			mScript.set_numFlakes(numFlakes);
		}
		
		private Allocation loadTexture(int id) {
	        final Allocation allocation =
	            Allocation.createFromBitmapResource(mRS, mRes,
	                id, Allocation.MipmapControl.MIPMAP_NONE,
	                Allocation.USAGE_GRAPHICS_TEXTURE);
	        return allocation;
	    }
}
