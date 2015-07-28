#include <stdlib.h>
#include <math.h>


float randomNumber(float min, float max)
{
  return min+(max-min)*rand()/(float)RAND_MAX;
}

void makeVector(float result[3], const float p1[3], const float p2[3])
{
  result[0]=p2[0]-p1[0];
  result[1]=p2[1]-p1[1];
  result[2]=p2[2]-p1[2];
}

void copyVector(const float source[3], float target[3])
{
  target[0]=source[0];
  target[1]=source[1];
  target[2]=source[2];
}

float normalizeVector(float v[3])
{
  float length;
  length=sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
  if (length>0)
  {
    v[0]/=length;
    v[1]/=length;
    v[2]/=length;
  }
  return length;
}

float dotProduct(const float v1[3], const float v2[3])
{
  return v1[0]*v2[0]+v1[1]*v2[1]+v1[2]*v2[2];
}

void crossProduct(float result[3], const float v1[3], const float v2[3])
{
  result[0]=v1[1]*v2[2]-v1[2]*v2[1];
  result[1]=v1[2]*v2[0]-v1[0]*v2[2];
  result[2]=v1[0]*v2[1]-v1[1]*v2[0];
}

float computeVectorLength(const float v[3])
{
  return sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
}


bool raySphereIntersection(const float rayOrigin[3], const float rayDirection[3],
                           const float sphereOrigin[3], float sphereRadius, 
                           float *distance)
{
  float c, v, d;
  float q[3];

  makeVector(q, rayOrigin, sphereOrigin);
  c=computeVectorLength(q);
  if (c<sphereRadius) return false;
  v=dotProduct(q, rayDirection);
  d=sphereRadius*sphereRadius-(c*c-v*v);
  
  if (d<0) return false;
  d=v-sqrt(d);

  *distance=d;  
  return true;
}

bool rayPlaneIntersection(const float rayOrigin[3], const float rayDirection[3],
                          const float planeOrigin[3], const float planeNormal[3],
                          float intersection[3])
{
  float numer, denom, d;

  // Calculate the distance to the intersection point.
  d=dotProduct(planeNormal, planeOrigin);
  numer=-dotProduct(planeNormal, rayOrigin)+d;
  denom=dotProduct(planeNormal, rayDirection);
  if (denom==0) return false;
  d=numer/denom;
  if (d<0) return false;

  // Store the intersection point.
  intersection[0]=rayOrigin[0]+d*rayDirection[0];
  intersection[1]=rayOrigin[1]+d*rayDirection[1];
  intersection[2]=rayOrigin[2]+d*rayDirection[2];

  // Return the result
  return true;
}


void multMatrixWithVector(float result[3], const float m[16], const float v[3])
{
  result[0]=m[0]*v[0]+m[4]*v[1]+m[8 ]*v[2]+m[12];
  result[1]=m[1]*v[0]+m[5]*v[1]+m[9 ]*v[2]+m[13];
  result[2]=m[2]*v[0]+m[6]*v[1]+m[10]*v[2]+m[14];
}



void multQuaternion(float result[4], const float q1[4], const float q2[4])
{
  result[0] = q1[0]*q2[0] - q1[1]*q2[1] - q1[2]*q2[2] - q1[3]*q2[3];
  result[1] = q1[0]*q2[1] + q1[1]*q2[0] + q1[2]*q2[3] - q1[3]*q2[2];
  result[2] = q1[0]*q2[2] + q1[2]*q2[0] + q1[3]*q2[1] - q1[1]*q2[3];
  result[3] = q1[0]*q2[3] + q1[3]*q2[0] + q1[1]*q2[2] - q1[2]*q2[1];
}

void quaternionFromAxisAndAngle(float q[4], const float axis[3], float angle)
{
  float sine;
  angle*=0.017453292;
  sine = sin(angle/2.0);
  q[0]=cos(angle/2.0);
  q[1]=axis[0]*sine;
  q[2]=axis[1]*sine;
  q[3]=axis[2]*sine;
}

void generateQuaternionMatrix(float m[16], const float q[4])
{
  float xx=q[1]*q[1];
  float xy=q[1]*q[2];
  float xz=q[1]*q[3];
  float xw=q[1]*q[0];
  float yy=q[2]*q[2];
  float yz=q[2]*q[3];
  float yw=q[2]*q[0];
  float zz=q[3]*q[3];
  float zw=q[3]*q[0];

  m[0] = 1 - 2 * ( yy + zz );
  m[1] =     2 * ( xy + zw );
  m[2] =     2 * ( xz - yw );
  m[4] =     2 * ( xy - zw );
  m[5] = 1 - 2 * ( xx + zz );
  m[6] =     2 * ( yz + xw );
  m[8] =     2 * ( xz + yw );
  m[9] =     2 * ( yz - xw );
  m[10]= 1 - 2 * ( xx + yy );
  m[3] = m[7] = m[11] = m[12] = m[13] = m[14] = 0;
  m[15]= 1;
}


void normalizeQuaternion(float q[4])
{
  float m=sqrt(q[0]*q[0]+q[1]*q[1]+q[2]*q[2]+q[3]*q[3]);

  if (m!=0)
  {
    q[0]/=m;
    q[1]/=m;
    q[2]/=m;
    q[3]/=m;
  }
}



bool rayInfiniteCylinderIntersection(const float rayOrigin[3], const float rayDirection[3],
                             const float cOrigin[3], const float cAxis[3], float radius,
                             float *distance)
{
	bool		hit=false;
	float		RC[3];
	float		d, t, s;
	float		n[3], O[3];
	float		ln;
	float in, out;

	RC[0] = rayOrigin[0] - cOrigin[0];
	RC[1] = rayOrigin[1] - cOrigin[1];
	RC[2] = rayOrigin[2] - cOrigin[2];
    crossProduct(n, rayDirection, cAxis);

	if  ( (ln = computeVectorLength(n)) == 0 ) return false;

	normalizeVector(n);
	d    = fabsf(dotProduct(RC, n));
	hit  = (d <= radius);

	if  (hit)
    {			
	    crossProduct(O, RC, cAxis);
	    t = - dotProduct(O, n) / ln;
	    crossProduct(O, n, cAxis);
	    normalizeVector(O);
	    s = fabsf (sqrt(radius*radius - d*d) / dotProduct(rayDirection, O));
	    in	 = t - s;
	    out = t + s;
	}

    *distance=in;
	return hit;
}




bool rayFiniteCylinderIntersection(const float rayOrigin[3], const float rayDirection[3],
                                   const float p1[3], const float p2[3], float radius,
                                   float *distance)
{
  float cAxis[3], ip[3], id[3];
  float dist;
  
  makeVector(cAxis, p1, p2);
  float len=normalizeVector(cAxis);
  
  if (!rayInfiniteCylinderIntersection(rayOrigin, rayDirection, 
                                       p1, cAxis, radius, &dist)) return false;

  ip[0]=rayOrigin[0]+ dist*rayDirection[0];
  ip[1]=rayOrigin[1]+ dist*rayDirection[1];
  ip[2]=rayOrigin[2]+ dist*rayDirection[2];  

  makeVector(id, p1, ip);
  float dot=dotProduct(id, cAxis);
  if (dot<0 || dot>len) return false;
  
  *distance=dist;
  return true;
}
