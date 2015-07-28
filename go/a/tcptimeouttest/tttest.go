package main
 
import (
"net"
"time"
)
 
func main() {
addr, _ := net.ResolveTCPAddr("tcp", "127.0.0.1:31432")
l, err := net.ListenTCP("tcp", addr)
if err != nil {
println(err.Error())
}
l.SetDeadline(time.Now().Add(2 * time.Second))
_, err = l.AcceptTCP()
if err != nil {
println(err.Error())
}
}