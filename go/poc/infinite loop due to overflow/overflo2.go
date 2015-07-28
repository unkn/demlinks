//from: http://play.golang.org/p/0EDi2vDnLv

package main

import "fmt"

func reverse(x byte) byte {
	x = (x&0x55)<<1|(x&0xAA)>>1
	x = (x&0x33)<<2|(x&0xCC)>>2
	x = (x&0x0F)<<4|(x&0xF0)>>4
	return x
}

func main() {
	//so, never use unsigned ints or equivalent ?
	for i := byte(0); i <= 255; i++ { //loop forever
		fmt.Println(i, reverse(i), i==reverse(reverse(i)))
	}
}
