#pragma version(1)
#pragma rs java_package_name(com.rayudu.flakes)

#include "rs_graphics.rsh"

typedef struct __attribute__((packed, aligned(4))) Flake{
	float2 position;
	float speed;
	float scale;
	float rotationY;
	float rotationSpeed;
}Flake_t;
Flake_t *flakes;
	
rs_mesh flakeQuad;
rs_program_vertex gProgVertex;
rs_program_fragment gProgFragment;

int64_t startTime;
int64_t prevTime;

int numFlakes = 0;

int screenW,screenH;
int droidW,droidH;
bool drawFps = true;
int frames = 0;

void setNumFlakes(int newNumFlakes);

void initScript(int w, int h,int initNumFlakes)
{
	screenW = w;
	screenH = h;
	setNumFlakes(initNumFlakes);
	prevTime = startTime = rsUptimeMillis();
}


void setNumFlakes(int newNumFlakes)
{
	int oldNumFlakes = numFlakes;
	numFlakes = newNumFlakes;
	if(newNumFlakes > oldNumFlakes)
	{
		for(int i = oldNumFlakes;i < numFlakes;i++)
		{
			Flake_t *flake = &flakes[i];
			flake->position.x = rsRand(screenW);
			flake->position.y = rsRand(screenH);
			flake->speed = 50.0f + rsRand(150.0f);
			flake->scale = 0.1f + rsRand(0.6f);
			flake->rotationY = rsRand(360.0f);
			flake->rotationSpeed = rsRand(90.0f) - 45.0f;
		}
	}	
}

void drawStats(){
	++frames;
	long nowTime = rsUptimeMillis();
	long deltaTime = nowTime - startTime;
	if(deltaTime > 1000){
		float secs = (float) deltaTime / 1000.0f;
		float fps = (float) frames / secs;
		startTime = nowTime;
		rsDebug("numFlakes, fps = ", numFlakes, fps, secs, frames);
		frames = 0;
	}
}			

int root(void){
	rsgClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	rsgBindProgramVertex(gProgVertex);
	rsgBindProgramFragment(gProgFragment);
	
	int64_t nowTime = rsUptimeMillis();
	float secs = (float) (nowTime - prevTime) / 1000.0f;
	prevTime = nowTime;
	
	for(int i=0;i<numFlakes;i++){
		Flake_t *flake = &flakes[i];
		
		float y = flake->position.y + flake->speed * secs;
		if(y > screenH){
			y = -50;
		}
		
		flake->position.y = y;
		flake->rotationY += flake->rotationSpeed * secs;
		
		rs_matrix4x4 transMatrix;
		//rsMatrixLoadIdentity(&transMatrix);
		rsMatrixLoadTranslate(&transMatrix, flake->position.x + droidW/2, flake->position.y + droidH/2, 0);
		rsMatrixScale(&transMatrix, flake->scale, flake->scale, 1.0f);
		rsMatrixRotate(&transMatrix, flake->rotationY, 0.0f, 0.0f , 1.0f);
		rsMatrixTranslate(&transMatrix, -droidW/2, -droidH/2, 0);
		rsgProgramVertexLoadModelMatrix(&transMatrix);
		
		rsgDrawMesh(flakeQuad);
	}
	
	if (drawFps){
		drawStats();
	}
	
	return 1;
}