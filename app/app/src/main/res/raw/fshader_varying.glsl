#extension GL_OES_EGL_image_external : require

precision mediump float;

uniform samplerExternalOES sTextureCam;
uniform samplerExternalOES sColorTexturePrimary;

uniform float uTime;
varying vec2 vTextureCoord;

uniform int uDetectRed;
uniform int uDetectYellow;
uniform int uDetectGreen;
uniform int uDetectBlue;
uniform int uDetectPurple;

vec3 hsv;
bool isSaturated;

float rand(vec2 co)
{ return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453); }

vec3 rgb2hsv(float r, float g, float b)
{
float K = 0.0;
float tmp;
 if (g < b)
  { tmp = g; g=b; b=tmp; K = -1.0; }
 if (r < g)
  { tmp = r; r=g; g=tmp; K = -2.9 / 6.9 - K; }
  float chroma = r - min(g, b);
  float h = abs(K + (g - b) / (6.0 * chroma + 1e-20));
  float s = chroma / (r + 1e-20);
  float v = r;
 return vec3(h, s, v);
}

void patternCheckers(){

        vec2 size = vec2(50.,50.);
        float slowTime = mod(uTime/100.0,1.0);
        float total = floor((vTextureCoord.x)*float(size.x)) +
                      floor((vTextureCoord.y)*float(size.y)) ;
        bool isEven = mod(total,2.0)==0.0;
        vec4 col = vec4(0.0,0.0,0.0,sin(uTime));
        vec4 col2 = vec4(1.0,1.0,1.0,1.0);
        if(isEven){
            gl_FragColor = col;
        }
}

mat2 rotate(float a) {
	float c = cos(a);
	float s = sin(a);
	return mat2(c, s, -s, c);
}

float hash(vec2 p) {
	return fract(4436.45 * sin(dot(p, vec2(45.45, 757.5))));
	}

void patternCamo() {
	vec2 uv = fract(vTextureCoord*20.);
	vec3 col = vec3(0.);

	uv *= 1.;
	//uv += uTime / 2.;

	vec2 i = floor(uv);
	vec2 f = fract(uv) - .5;


	f *= rotate(floor(hash(i) * 4.) * acos(-1.0) / 2.);

	float d = dot(f, vec2(1));
	col += smoothstep(.015, .0, d);
	if(col.x==1.0){
	    gl_FragColor = vec4(col, 1.);
	}
}

float circle(in vec2 _st, in float _radius){
    vec2 l = _st-vec2(0.5);
    return smoothstep(_radius-(_radius*0.01),
                         _radius+(_radius*0.01),
                         dot(l,l)*4.0);
}

void patternCircles(){
        vec2 sizeRelative = vec2(0.02,0.04);
        vec2 spacing = vec2(0.01);
        vec2 st = vTextureCoord / (spacing+sizeRelative);
        st = fract(st);
        float c = circle(st,0.5-spacing.x);
        if(c == 0.0){
         gl_FragColor = vec4(vec3(c), 1.0);
        }
}

float cross(in vec2 _st, in float _thickness){
    float y = step(abs(_st.x-_st.y),_thickness);
    float z = step(abs(1.0-_st.x-_st.y),_thickness);

    return min(y+z,1.0);
}

void patternCrosses(){
        float thickness = 0.1;
        float definition = 0.05;
        vec2 st = vTextureCoord / (definition);
        st = fract(st);
        float c = cross(st,thickness);
        if(c == 1.0){
         gl_FragColor = vec4(vec3(c), 1.0);
        }
}





void detectRed(){
 if (uDetectRed!=0){
         //If fragment is red
         if( (hsv.x > 0.9 || hsv.x < 0.04
         &&  hsv.y >0.30
         &&  hsv.z > 0.30 && hsv.z <0.70
         &&  abs(hsv.y-hsv.z) < 0.30)){
             //gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
             //gl_FragColor = texture2D(sColorTexturePrimary, vTextureCoord);
            patternCheckers();
         }
    }
}

void detectYellow(){
 if (uDetectYellow!=0){
         //If fragment is yellow
         if( (hsv.r > 0.04 && hsv.r < 0.25
         &&  hsv.g >0.30
         &&  hsv.b > 0.25 && hsv.b <0.85
         &&  abs(hsv.g-hsv.b) < 0.30)){
            // gl_FragColor = vec4(1.0, 1.0, 0.0, 1.0);
            patternCircles();
         }
    }

}

void detectGreen(){
 if (uDetectGreen!=0){
         //If fragment is yellow
         if( (hsv.r > 0.20 && hsv.r < 0.40
          &&  hsv.g >0.20
          &&  hsv.b > 0.10 && hsv.b <0.70
          &&  abs(hsv.g-hsv.b) < 0.40)

           ||

          (hsv.r > 0.40 && hsv.r < 0.50
          &&  hsv.g >0.60
          &&  hsv.b > 0.50 && hsv.b <0.70
          &&  abs(hsv.g-hsv.b) < 0.40)

           ){
              patternCamo();
         }
     }

}

void detectBlue(){
if (uDetectBlue!=0){
         //If fragment is yellow
         //45-72
         if(hsv.r > 0.40 && hsv.r < 0.80
                      &&  hsv.g >0.35
                      &&  hsv.b > 0.05 && hsv.b <0.70
                      //&&  abs(hsv.g-hsv.b) < 0.30
                      ){
              patternCrosses();
         }
     }

}

void detectPurple(){
 if (uDetectPurple!=0){
         //If fragment is yellow
         if( (hsv.r > 0.72 && hsv.r < 0.9 && isSaturated)){
              patternCamo();
         }
     }


}

void main(){
    vec4 cam = texture2D(sTextureCam, vTextureCoord);
    gl_FragColor = cam;


    hsv = rgb2hsv(cam.r, cam.g, cam.b);
    isSaturated = hsv.g >0.30 && hsv.b > 0.3 && hsv.b <0.7;
    if(uDetectRed!=0){detectRed();}
    if(uDetectYellow!=0){detectYellow();}
    if(uDetectGreen!=0){detectGreen();}
    if(uDetectBlue!=0){detectBlue();}
    if(uDetectPurple!=0){detectPurple();}

}







/*
    if ( abs(hsv.r-1.)< 0.30 && abs(hsv.g -1.) <0.9 && abs(hsv.b-1.)<0.8 ){
            float rand = rand( vec2(uTime, uTime) );
            gl_FragColor = vec4(( vec3(rand,rand,rand)*0.6 + cam.xyz* 0.4),1.0);
      }*/
