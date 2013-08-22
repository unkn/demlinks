package main

import (
	"fmt"
	"time"
)

func main() {
	sz := 100000
	sl1 := make([]int32, sz)

	//var j int32
	l := int32(len(sl1))
	for j := int32(0); j < l; j++ {

		sl1[j] = j
	}

	sl2 := make([]int32, sz)

	l = int32(len(sl2))
	for j := int32(0); j < l; j++ {
		sl2[j] = j * 2
	}

	start := time.Now()
	var num int64 = 0
	var n int32
	for i := range sl1 {

		n = sl1[i]
		for j := range sl2 {

			num += int64(n * sl2[j])
		}
	}

	et := time.Since(start)
	fmt.Println(et)
	fmt.Println(num)
}
