# dynamik

Dynamik is a toy, dynamically-typed language, written in Kotlin.   


### Features

- [x] Expressions with Strings and Integers. (At present the result of operations with Integers are implicitly converted to Doubles)
- [x] (Im)mutable variable bindings with val/var
- [x] If/Else statements
- [x] For/While loops
- [x] Functions 
- [x] Comments
- [x] Memoization builtin
- [x] Collections (list, map)
- [x] Classes 
- [x] Builtin timer
- [x] Repl


### How to Run 

```
./gradlew build 
java -jar build/libs/dynamik-1.0-SNAPSHOT-all.jar --file=<filename>
```

### Examples 


#### Fibonacci 
```
//the @memo wrapper caches the output of fib against its input and uses it 
//when needed
@memo
fn fib(n) {
    if (n<2) { return n;}
    return fib(n-1) + fib(n-2);
}

val res = fib(100);
print(res);
```




#### Variable Bindings 
```

//create a "final" variable 
val hello = "world";

/* can't do this (throws a ValError) 
hello = "world";
*/

var foo = 1;
foo = 2; //works 
```


#### Classes 
```
class Math {
    fn abs(x) { 
        if (x<0) { return -x; }
        return x;
     }
}

val math = Math();
val plus_one = math.abs(-1);
print(plus_one);
```



#### Containers 
```
val stuff = list();
stuff.add("foo");
stuff.add("bar");
for (var i =0;i<stuff.len();i = i+1) {
    print(stuff.get(i));
}
```










