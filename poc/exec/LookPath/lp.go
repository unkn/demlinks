// LookPath on Windows doesn't work as it should, issue created: http://code.google.com/p/go/issues/detail?id=6224
//go run lp.go

package main

import "os"
import "os/exec"

func main() {
	testEXE()
	testBAT()
}

func setup(extensions string) {
	setEnv("PATHEXT", extensions)
	setEnv("PATH", "C:\\windows\\system32")
}

func findWhere() {
	lookPath("where")
	lookPath("where.exe")
	lookPath("c:\\windows\\system32\\where.exe")
}

func testEXE() { //all work
	setup(".EXE")
	findWhere()
}

func testBAT() { //all fail
	setup(".BAT")
	findWhere()
}

func lookPath(file string) {
	if ffile, err := exec.LookPath(file); err != nil {
		println("Not found:", err.Error())
	} else {
		println("    Found:", ffile)
	}
}

func setEnv(key, value string) {
	os.Setenv(key, value)
	if now_val := os.Getenv(key); value != now_val {
		println("Couldn't set `", key, "` env var, current value `", now_val, "`, wanted value `", value, "`")
	}
}
