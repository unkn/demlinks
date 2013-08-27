package demlinks

import (
	"fmt"
	"os"
	"os/exec"
	"path/filepath"
	"testing"
	// "time"
)

func TestNothing(t *testing.T) {

	//temp := os.Getenv("TEMP")

	// tmp, err1 := ioutil.TempDir("", "test1")
	// if err1 != nil {
	// 	t.Fatalf("TempDir failed: %v", err1)
	// 	// } else {
	// 	// 	defer func() {
	// 	// 		fmt.Println(os.RemoveAll(tmp))
	// 	// 	}()
	// }
	temp := filepath.Join(os.Getenv("TEMP"), "test1")
	os.Mkdir(temp, os.ModePerm)
	defer func() { fmt.Println("testerrrr:", os.RemoveAll(temp)) }()

	what := filepath.Join(temp, "a.exe")
	//fmt.Println(temp, what)

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
		//fmt.Println("waited:", c.Wait())
		select {
		case err = <-done:
			// ok
		}
		fmt.Println("after else:", err)
	}
	//time.Sleep(1000 * time.Millisecond)

	//os.OpenFile(what, flag, perm)

	//filepath.Join(tmp,what)

	//t.FailNow()
}
