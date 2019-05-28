# dynamik

Dynamik is a toy, dynamically-typed language, written in Kotlin.   


### Features

- [x] Expressions with Strings and Integers. (At present, the result of an operation with Integers is implicitly converted to a Double.)
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

//to execute a file 
java -jar build/libs/dynamik-1.0-SNAPSHOT-all.jar --file=<filename>

//to start repl 
java -jar build/libs/dynamik-1.0-SNAPSHOT-all.jar --repl

```

### Examples 

#### Fibonacci 
```
//the @memo annotation caches the output of fib against its input and uses it 
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

// can't do this (throws ValError) 
// hello = "world";


var foo = 1;
foo = 2; //works 
```


#### Classes
```
class Animal(name) {
    fn change_name(new_name) {
        this.name = new_name;
    }
}

val dog = Animal("foo");
dog.change_name("bar");
print(dog.name);

```



#### Collections
```
//initialzie a list with one value. 
val my_list  = list(0); 
for (var i=1;i<=10;i = i+1) {
    my_list.add(i);
}

val my_map = map();
my_map.insert("hello", my_list.get(1));
assert(my_map.get("hello") == 1);

```


### Stability Caveats
This is still a work-in-progress, with a few known edge-case bugs in the parser implementation. As such, after fixing those, I will probably port the recursive descent parser to one that uses parser combinators. 







