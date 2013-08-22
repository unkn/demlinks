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
	//var n int32
	//l = int32(len(sl1))
	for _, i := range sl1 {
		//n := i
		//l2 := int32(len(sl2))
		var acc int64 //from 43 to 32 sec  if using this
		for _, j := range sl2 {
			acc += int64(i * j)
		}
		num += acc
	}

	et := time.Since(start)
	fmt.Println(et)
	fmt.Println(num)
}
