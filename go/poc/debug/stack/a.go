package main

import (
	"fmt"
	"runtime/debug"
)

func main() {
	fmt.Println(string(debug.Stack()))
}
