package demlinks

import (
	"fmt"
	"os"
	"os/exec"
	"path/filepath"
	"testing"
	"time"
)

func TestNothing(t *testing.T) {
	//t.FailNow() //XXX: uncomment this when executing `go test -work` for the first time
	//XXX: then rename the gobuild#### folder to `test1` and make a backup of it somewhere so you can get it back quick in this same place
	//the comment this and execute `go test`
	//notice the test1 folder remained with the .exe inside
	//run `go test` again and it's gone
	//uncomment the time.Sleep below to make it always work
	//it's a matter of time... unsure if windows is delaying the .exe unlocking after execute or what
	// I do remember it used to keep them locked for 60 seconds some time ago, not sure why but I fixed that once
	// the point is though, that's possible that windows is keeping it locked a few ms more even after it finished execution
	// (PS: i've no antivirus or defense+ programs to keep it locked, maybe virus then? or windows)

	temp := filepath.Join(os.Getenv("TEMP"), "test1")
	os.Mkdir(temp, os.ModePerm)
	defer func() {
		//time.Sleep(2000 * time.Millisecond) //XXX: uncomment this to have delete always work!
		fmt.Println("testerrrr:", os.RemoveAll(temp))
		_ = time.ANSIC //avoid import "time" unused error
	}()

	what := filepath.Join(temp, "github.com", "demlinks", "demlinks", "poc", "simpletest", "_test", "simpletest.test.exe")

	c := exec.Command(what)
	c.Stdout = os.Stdout
	c.Stderr = os.Stderr
	//c.Run()
	err := c.Start()
	if err != nil {
		fmt.Println(err)
	} else {
		done := make(chan error)
		go func() {
			done <- c.Wait()
			fmt.Println("after wait2!")
		}()
		select {
		case err = <-done:
			// ok
		}
		fmt.Println("after else:", err)
	}
}
