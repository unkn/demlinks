//this code is from here: https://groups.google.com/d/msg/golang-nuts/LW2l-LO5QW8/cHj7DSV7Wl8J
package main

import (
	"flag"
	"fmt"
	"log"
	"os"
	"runtime"
	"runtime/pprof"
	"time"
)

const (
	TileDim    = 50
	WidMin     = 2
	RestWidMax = 8
	NumLevs    = 800
	NumTries   = 50000
)

type Tile struct {
	X uint32
	Y uint32
	T uint32
}

type Room struct {
	X uint32
	Y uint32
	W uint32
	H uint32
	N uint32
}

type Lev struct {
	ts *[]Tile
	rs []Room
}

func Rand(seed uint32) uint32 {
	seed <<= 1
	sext := uint32(int32(seed)>>31) & 0x88888eef
	seed ^= sext ^ 1
	return seed
}

func CheckColl(x, y, w, h uint32, rs []Room) bool {
	for i := range rs {
		r := &rs[i]
		if (r.X+r.W+1) < x || r.X > (x+w+1) {
			continue
		}
		if (r.Y+r.H+1) < y || r.Y > (y+h+1) {
			continue
		}
		return true
	}
	return false
}

func MakeRoom(count uint32, seed uint32) []Room {
	rs := make([]Room, 100)
	counter := uint32(0)
	for i := uint32(0); i < count; i++ {
		seed = Rand(seed)
		x := seed % TileDim
		seed = Rand(seed)
		y := seed % TileDim
		seed = Rand(seed)
		w := seed%RestWidMax + WidMin
		seed = Rand(seed)
		h := seed%RestWidMax + WidMin
		if x+w >= TileDim || y+h >= TileDim || x*y == 0 {
			continue
		}
		iscrash := CheckColl(x, y, w, h, rs[0:counter])
		if iscrash == false {
			rs[counter] = Room{x, y, w, h, counter}
			counter++
		}
		if counter == 99 {
			break
		}
	}
	return rs[0:counter]
}

func Room2Tiles(r *Room, ts *[]Tile) {
	x := r.X
	y := r.Y
	w := r.W
	h := r.H
	for xi := x; xi <= x+w; xi++ {
		for yi := y; yi <= y+h; yi++ {
			num := yi*TileDim + xi
			(*ts)[num].T = 1
		}
	}
}

func PrintLev(l *Lev) {
	for i, t := range *l.ts {
		fmt.Printf("%v", t.T)
		if i%(TileDim) == 49 && i != 0 {
			fmt.Print("\n")
		}
	}
}

func godo(seed chan uint32, levchan chan *Lev) {
	for s := range seed {
		rs := MakeRoom(NumTries, s)
		ts := make([]Tile, 2500)
		for i := uint32(0); i < 2500; i++ {
			ts[i] = Tile{X: i % TileDim, Y: i / TileDim, T: 0}
		}
		for _, r := range rs {
			Room2Tiles(&r, &ts)
		}
		lev := &Lev{&ts, rs}
		levchan <- lev
	}
}

var vflag = flag.Int("v", 18, "Random Seed")
var w = flag.Int("w", 8, "workers")
var c = flag.Int("cpus", 2, "cpus")
var cpuprofile = flag.String("cpuprofile", "", "save profile in this file")

func main() {
	flag.Parse()

	if *cpuprofile != "" {
		f, err := os.Create(*cpuprofile)
		if err != nil {
			log.Fatal(err)
		}
		pprof.StartCPUProfile(f)
		defer pprof.StopCPUProfile()
	}

	start := time.Now()

	seedchan := make(chan uint32, NumLevs)
	levchan := make(chan *Lev, NumLevs)

	fmt.Printf("Random seed: %v\n", *vflag)
	seed := ^uint32(*vflag)

	runtime.GOMAXPROCS(*c)

	for i := 0; i < *w; i++ {
		go godo(seedchan, levchan)
	}

	for i := uint32(0); i < NumLevs; i++ {
		seedchan <- seed * (i + 1) * (i + 1)
	}
	close(seedchan)

	templ := Lev{}
	for i := 0; i < NumLevs; i++ {
		x := <-levchan
		if len(x.rs) > len(templ.rs) {
			templ = *x
		}
	}
	PrintLev(&templ)
	end := time.Now()
	fmt.Printf("Time: %s\n", end.Sub(start))
}
