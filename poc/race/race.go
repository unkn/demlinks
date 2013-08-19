//this code is from here: https://groups.google.com/d/msg/golang-nuts/NDQBTV9lH7s/gdEn7iqjpDUJ
//demonstrates a race condition, to run: go run -race race.go
//you can also do: go build
// then run race.exe

package main

import (
	"fmt"
	"math/rand"
//	_ "net/http/pprof"
	"time"
)

func main() {
/*	go func() {
		log.Println(http.ListenAndServe("localhost:6060", nil))
	}()*/

	x := fanIn(boring("A"), boring("B"), boring("C"), boring("D"))
	for i := 0; i < 20; i++ {
		fmt.Println(<-x)
	}
	fmt.Println("You're both boring; I'm leaving.")
}

func fanIn(inputs ...<-chan string) <-chan string {
	c := make(chan string)
	for _, input := range inputs {
		go func() {
			for {
				c <- <-input
			}
		}()
	}
	return c
}

func boring(msg string) <-chan string {
	c := make(chan string)
	go func() { // We launch the goroutine from inside the function.
		for i := 0; ; i++ {
			c <- fmt.Sprintf("%s %d", msg, i)
			time.Sleep(time.Duration(rand.Intn(1e3)) * time.Millisecond)
		}
	}()
	return c // Return the channel to the caller.
}
