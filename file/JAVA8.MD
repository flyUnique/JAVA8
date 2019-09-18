# 流的使用

## 流处理

> 流是一系列数据项，一次只生成一项。

## 行为参数化

- 函数可以作为参数传入另外一个函数
- 函数可以作为返回值
- 函数作为一等公民

## lambda表达式（(parameters) -> (expressions)）

-  匿名——我们说匿名，是因为它不像普通的方法那样有一个明确的名称：写得少而想得多！ 
-  函数——我们说它是函数，是因为Lambda函数不像方法那样属于某个特定的类。但和方法一样，Lambda有参数列表、函数主体、返回类型，还可能有可以抛出的异常列表。 
-  传递——Lambda表达式可以作为参数传递给方法或存储在变量中。 
-  简洁——无需像匿名类那样写很多模板代码。 

## 函数式接口（@FunctionalInterface）

> 函数式接口就是只定义一个抽象方法的接口。

| 接口                | 函数描述符          | 说明    |
| ----------------- | -------------- | ----- |
| Predicate<T>      | T->boolean     | 谓词    |
| Consumer<T>       | T->void        | 消费者   |
| Function<T, R>    | T->R           | 函数    |
| Supplier<T>       | ()->T          | 生产者   |
| UnaryOperator<T>  | T->T           | 单值操作  |
| BinaryOperator<T> | (T,T)->T       | 双值操作  |
| BiPredicate<L,R>  | (L,R)->boolean | 双值谓词  |
| BiConsumer<T,U>   | (T,U)->void    | 双值消费者 |
| BiFunction<T,U,R> | (T,U)->R       | 双值函数  |

- 原始类型特化

> 装箱后的值本质上就是把原始类型包裹起来，并保存在堆里。因此，装箱后的值需要更多的内存，并需要额外的内存搜索来获取被包裹的原始值。

## 方法引用（::）

> 仅仅涉及单一方法的Lambda的语法糖:方法引用的签名必须和上下文类型匹配

- (1) 指向静态方法的方法引用
- (2) 指向任意类型实例方法 的方法引用
- (3) 指向现有对象的实例方法的方法引用

## 流

> 从支持数据处理操作的源生成的元素序列。
>
> 集合讲的是数据，流讲的是计算。
>

- 只能遍历一次。
- 流水线。
- 内部迭代。
- 中间操作与终端操作。

## 流的使用（API）

- 筛选（filter、distinct、skip、limit）
- 映射（map、flatMap）
- 查找和匹配（allMatch、anyMatch、noneMatch、findFirst、findAny）
- 规约（reduce）

## 收集器（Collectors）

-  将流元素归约和汇总为一个值 
  - counting
  - maxBy、minBy
  - summingInt、summingLong、summingDouble、averagingInt、averagingLong、averagingDouble、summarizingInt、summarizingLong、summarizingDouble
  - joining
  - reducing
-  元素分组 
  - groupingBy
-  元素分区
  - partitioningBy

### 接口

```java
public interface Collector<T, A, R> { 
    // 建立新的结果容器
    Supplier<A> supplier(); 
    // 将元素添加到结果容器
    BiConsumer<A, T> accumulator();
    // 对结果容器应用最终转换:通常,累加器对象恰好符合预期的最终结果，因此无需进行转换
    Function<A, R> finisher(); 
    // 合并两个结果容器
    BinaryOperator<A> combiner(); 
    // 定义了收集器的行为
    Set<Characteristics> characteristics(); 
} 
```

-  T是流中要收集的项目的泛型。 
-  A是累加器的类型，累加器是在收集过程中用于累积部分结果的对象。 
-  R是收集操作得到的对象（通常但并不一定是集合）的类型。

#### Characteristics

-  UNORDERED——归约结果不受流中项目的遍历和累积顺序的影响。 
-  CONCURRENT——accumulator函数可以从多个线程同时调用，且该收集器可以并行归约流。如果收集器没有标为UNORDERED，那它仅在用于无序数据源时才可以并行归约。 

-  IDENTITY_FINISH——这表明完成器方法返回的函数是一个恒等函数，可以跳过。这种情况下，累加器对象将会直接用作归约过程的最终结果。这也意味着，将累加器A不加检查地转换为结果R是安全的。

```java
List<Dish> dishes = menuStream.collect( 
                        ArrayList::new,  
                        List::add,  
                        List::addAll);
```

## 并行流(parallel)

> 最后一次parallel或sequential调用会影响整个流水线。

- 如果没有性能瓶颈，无需使用并行
- 不清楚的前提下，做测试
- 自动装箱和拆箱操作会大大降低性能
- 依赖于元素顺序的操作，它们在并行流上执行的代价非常大
- 在遍历数据本身与业务中进行权衡
- 较小的数据量，选择并行流几乎从来都不是一个好的决定
- 考虑流背后的数据结构是否易于分解

## 分支/合并框架（Fork/Join）

- RecursiveTask<R>
  - compute()
- RecursiveAction
- ForkJoinPool
  - invoke(task)

```
if (任务足够小或不可分) { 
    顺序计算该任务  
} else { 
    将任务分成两个子任务 
    递归调用本方法，拆分每个子任务，等待所有子任务完成 
    合并每个子任务的结果 
} 
```

> 分出大量的小任务一般来说都是一个好的选择，默认线程数量Runtime.getRuntime().availableProcessors()。
>
> 修改线程数量 System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism","12");

#### 最佳做法

- 对一个任务调用join方法会阻塞调用方，直到该任务做出结果。因此，有必要在两个子任务的计算都开始之后再调用它。
- 不应该在RecursiveTask内部使用ForkJoinPool的invoke方法。
- 对子任务调用fork方法可以把它排进ForkJoinPool。

#### 工作窃取（work stealing）

> 在池中的工作线程之间重新分配和平衡任务

## Spliterator(可分迭代器)

> 自定义拆分规则，为并行执行而设计。

```java
// T是Spliterator遍历的元素的类型
public interface Spliterator<T> { 
    // 按顺序一个一个使用Spliterator中的元素，并且如果还有其他元素要遍历就返回true
    boolean tryAdvance(Consumer<? super T> action); 
    // 把一些元素划出去分给第二个Spliterator（由该方法返回），让它们两个并行处理
    Spliterator<T> trySplit(); 
    // 估计还剩下多少元素要遍历
    long estimateSize(); 
    // Spliterator的特性
    int characteristics(); 
} 
```

#### Spliterator的特性

- ORDERED:  元素有既定的顺序（例如List） ，因此Spliterator在遍历和划分时也会遵循这一顺序 
- DISTINCT:  对于任意一对遍历过的元素x和y，x.equals(y)返回false 
- SORTED:  遍历的元素按照一个预定义的顺序排序 
- SIZED:  该Spliterator由一个已知大小的源建立（例如Set），因此estimatedSize()返回的是准确值 
- NONNULL:  保证遍历的元素不会为null 
- IMMUTABLE:  Spliterator的数据源不能修改。这意味着在遍历时不能添加、删除或修改任何元素 
- CONCURRENT:  该Spliterator的数据源可以被其他线程同时修改而无需同步 
- SUBSIZED:  该Spliterator和所有从它拆分出来的Spliterator都是SIZED

## 备注

- 任何函数式接口都不允许抛出受检异常（checked exception）
- 特殊的void兼容规则

> 如果一个Lambda的主体是一个语句表达式， 它就和一个返回void的函数描述符兼容（当然需要参数列表也兼容）

- 避免共享可变状态，确保并行Stream得到正确的结果

# 重构

## 设计模式

> 设计经验的归纳总结被称为设计模式

-  策略模式 (strategy)
-  模板方法 (template method)
-  观察者模式(observer)
-  责任链模式(chain of responsablity)
-  工厂模式 (abstract factory)

### 策略模式(strategy)

> 策略模式代表了解决一类算法的通用解决方案，你可以在运行时选择使用哪种方案。

 一个代表某个算法的接口（它是策略模式的接口）。 
 一个或多个该接口的具体实现，它们代表了算法的多种实现（比如，实体类ConcreteStrategyA或者ConcreteStrategyB）。 
 一个或多个使用策略对象的客户。

# 默认方法

> 其一，Java 8允许在接口内声明静态方法。其二，Java 8引入了一个新功能，叫默认方法，通过默认方法你可以指定接口方法的默认实现。
> 让类可以自动地继承接口的一个默认实现。

## 菱形继承解决方案

- (1) 类中的方法优先级最高。类或父类中声明的方法的优先级高于任何声明为默认方法的优先级。 
- (2) 如果无法依据第一条进行判断，那么子接口的优先级更高：函数签名相同时，优先选择拥有最具体实现的默认方法的接口，即如果B继承了A，那么B就比A更加具体。 
- (3) 最后，如果还是无法判断，继承了多个接口的类必须通过显式覆盖和调用期望的方法，显式地选择使用哪一个默认方法的实现。

> Class.super.method();

# Optional

## NULL产生的问题

-  它是错误之源。 

> NullPointerException是目前Java程序开发中最典型的异常。 

-  它会使你的代码膨胀。 

> 它让你的代码充斥着深度嵌套的null检查，代码的可读性糟糕透顶。 

-  它自身是毫无意义的。 

> null自身没有任何的语义，尤其是，它代表的是在静态类型语言中以一种错误的方式对缺失变量值的建模。 

-  它破坏了Java的哲学。 

> Java一直试图避免让程序员意识到指针的存在，唯一的例外是：null指针。 

-  它在Java的类型系统上开了个口子。 

> null并不属于任何类型，这意味着它可以被赋值给任意引用类型的变量。这会导致问题，原因是当这个变量被传递到系统中的另一个部分后，你将无法获知这个null变量最初的赋值到底是什么类型。

# CompletableFuture

## Future

> 建模了一种异步计算，返回一个执行运算结果的引用，当运算结束后，这个引用被返回给调用方。

### 局限性

-  将两个异步计算**合并**为一个——这两个异步计算之间相互独立，同时第二个又依赖于第一个的结果。 
-  等待Future集合中的所有任务都完成。 
-  仅等待Future集合中最快结束的任务完成（有可能因为它们试图通过不同的方式计算同一个值），并返回它的结果。
-  通过编程方式完成一个Future任务的执行（即以手工设定异步操作结果的方式）。
-  应对Future的完成事件（即当Future的完成事件发生时会收到通知，并能使用Future计算的结果进行下一步的操作，不只是简单地阻塞等待操作的结果）。

## CompletableFuture

```java
// 默认使用通用线程池，为CPU核个数
CompletableFuture<T> completeFuture = new CompletableFuture<>();
// 收集截执行结果
completeFuture.complete(T);
// 收集异常信息
completeFuture.completeExceptionally(Throwable);
// 异步执行，自动收取结果和异常信息
completeFuture.supplyAsync(Supplier<T>);
// 获取结果
completeFuture.join();

// 定制线程执行器
completeFuture.supplyAsync(Supplier<T>, Excutor);

completeFuture.thenApply(Function<T,U>);
completeFuture.thenCompose(Function<T, CompletionStage);
completeFuture.thenCombine(CompletionStage, BiFunction<T, U, V>);

completeFuture.thenAccept(Consumer);

// 等待所有任务执行完毕
CompletableFuture.allOf(CompletableFuture[]).join();
// 等待任意一个任务执行完毕
CompletableFuture.anyOf(CompletableFuture[]).join();
```

### 线程数量

Nthreads = NCPU * UCPU * (1 + W/C) 
其中： 
- ❑NCPU是处理器的核的数目，可以通过Runtime.getRuntime().availableProcessors()得到
- ❑UCPU是期望的CPU利用率（该值应该介于0和1之间）
- ❑W/C是等待时间与计算时间的比率

# 日期

- LocalDate
- LocalTime
- Instant
- Duration
- Period
