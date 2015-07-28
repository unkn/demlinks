//inspired from: http://tour.golang.org/#9
package main

import "fmt"

func swap(x, y string) (string, string) {
	return y, x
}

func main() {
	a := "a"
	b := "b"
	a, b = b, a
	fmt.Println(a, b)

	a = "x"
	b = "y"
	a, b = swap(a, b)
	fmt.Println(a, b)
}
