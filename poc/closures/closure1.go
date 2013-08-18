//code inspired from: https://gist.github.com/freeformz/4746274/a56e6b4558b384459c1188f737ec652709e519fe#anonymous-functions--closures


package main

import "fmt"

func foo() func() {
	var x = 5
	a := func() {
		fmt.Println("I can reference x because I am a closure, x=", x) //6
	}
	defer func() {
		fmt.Println("Before changing: ",x)
		x = 6
		fmt.Println("Before foo() exit: ",x)
	}()
	fmt.Println("Before return: ",x)
	return a
}

func main() {
	f := foo()
	f()
}
