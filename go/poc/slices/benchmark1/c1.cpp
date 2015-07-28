
#include <iostream>
#include <ctime>
#include <ratio>
#include <chrono>


static const int kSize = 100000;
static int *kArr1;
static int *kArr2;
static const int kRunTime = 10; 

static long operation() {
  long num = 0L;
  for (int i = 0; i < kSize; i++) {
    int n = kArr1[i];
    for (int j = 0; j < kSize; j++) {
        num += n * kArr2[j];
    }
  }
  return num;
}

int main() {
  using namespace std::chrono;
  typedef high_resolution_clock Clock;

  kArr1 = new int[kSize];
  kArr2 = new int[kSize];
  for (int i = 0; i < kSize; i++) {
    kArr1[i] = i;
    kArr2[i] = i * 2;
  }

  double total_time = 0.0;
  for (int i = 0 ; i < kRunTime; ++i) {
    auto t1 = Clock::now();
    long num = operation();
    double time_span = (duration_cast<duration<double>>(Clock::now() - t1)).count();
    total_time += time_span;
    std::cout << "Time taken for operation " << i << " : " << time_span << " seconds." << std::endl;
    std::cout << "Result is " << num << std::endl;
  }
  std::cout << "\nAverage time: " << (total_time/kRunTime) << "seconds." << std::endl;
  return 0;
}