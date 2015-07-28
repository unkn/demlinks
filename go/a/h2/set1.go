package main

import (
	"fmt"
	"github.com/fatih/goset"
	"strconv"
	"sync"
)

func main() {
	var wg sync.WaitGroup // this is just for waiting until all goroutines finish

	// Initialize our Set
	s := goset.New()

	// Add items concurrently (item1, item2, and so on)
	for i := 0; i < 50; i++ {
		wg.Add(1)
		go func(i int) {
			item := "item" + strconv.Itoa(i)
			fmt.Println("adding", item)
			s.Add(item)
			wg.Done()
		}(i)
	}

	// Wait until all concurrent calls finished and print our set
	wg.Wait()
	fmt.Println(s)
}
