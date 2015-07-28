package main

func main() {
	println(c())
}

//from: http://blog.golang.org/defer-panic-and-recover
func c() (i int) {
	defer func() { i++ }()
	return 1
}
