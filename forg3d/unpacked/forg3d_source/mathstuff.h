#ifndef __matikka_h__
#define __matikka_h__


float randomNumber(float min, float max);
void makeVector(float result[3], const float p1[3], const float p2[3]);
void copyVector(const float source[3], float target[3]);
float normalizeVector(float v[3]);
float dotProduct(const float v1[3], const float v2[3]);
void crossProduct(float result[3], const float v1[3], const float v2[3]);

bool raySphereIntersection(const float rayOrigin[3], const float rayDirection[3],
                           const float sphereOrigin[3], float sphereRadius,
                           float *distance);
bool rayPlaneIntersection(const float rayOrigin[3], const float rayDirection[3],
                          const float planeOrigin[3], const float planeNormal[3],
                          float intersection[3]);
                           

void multMatrixWithVector(float result[3], const float m[16], const float v[3]);

void multQuaternion(float result[4], const float q1[4], const float q2[4]);
void quaternionFromAxisAndAngle(float q[4], const float axis[3], float angle);
void generateQuaternionMatrix(float m[16], const float q[4]);
void normalizeQuaternion(float q[4]);


bool rayInfiniteCylinderIntersection(const float rayOrigin[3], const float rayDirection[3],
                             const float cOrigin[3], const float cAxis[3], float radius,
                             float *distance);
                             
bool rayFiniteCylinderIntersection(const float rayOrigin[3], const float rayDirection[3],
                                   const float p1[3], const float p2[3], float radius,
                                   float *distance);
                             


#endif
