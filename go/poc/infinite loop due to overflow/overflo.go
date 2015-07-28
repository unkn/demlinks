//code from here: https://code.google.com/p/go/issues/detail?id=6126

package main

func main() {
	var sum, i, end float32

	end = 1E7
	sum = 0
	for i = 0; i <= end; i++ {
		sum += i
	}
	print("end=", end, "  sum=", sum, "\n")

	sum = 0
	for i = 0; i <= 1E8; i++ {
		sum += i
	}
	print("end=", end, "  sum=", sum, "\n")
}
