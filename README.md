# dynamik

Dynamik is a toy, dynamically-typed language, written in Kotlin.   


### Features

- [x] Expressions with Strings and Integers (implicitly converted to Doubles for simplicity) 
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




