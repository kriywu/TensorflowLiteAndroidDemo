## Lite的使用

Lite的使用主要分为以下两步，下面是我做的一个简单应用的关键步骤。

这个应用

- 输入：一个数字
- 输出：将这个数字加1

例如：

- 输入：1 
- 输出:   2



### 1 训练模型与导出

这里不能使用复杂的算子，但基本上保留了所有的数值运算，这里以数字相加代表模型训练。

```python
import numpy as np
import time
import math
import tensorflow as tf

input = tf.placeholder(tf.int32, shape = 0, name = 'input') # 输入

output = input + 1 # 训练（这里用加1表示）

with tf.Session() as sess:
    init_op = tf.global_variables_initializer()
    sess.run(init_op)
    converter = tf.lite.TFLiteConverter.from_session(sess, [input], [output]) 
    tflite_model = converter.convert() # 转化为tflite
    open("model.tflite", "wb").write(tflite_model) # 保存 .tflite 文件
```

### 2 模型使用

1. 将“.tflte” 文件放入 assets目录下

   ![保存](C:\Users\rengao_wu\AppData\Roaming\Typora\typora-user-images\1559828191560.png)

2. 读取模型文件，初始化interpreter解释器

   ```java
   AssetFileDescriptor fileDescriptor = getAssets().openFd("model.tflite");
   FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
   FileChannel fileChannel = inputStream.getChannel();
   long startOffset = fileDescriptor.getStartOffset();
   long declaredLength = fileDescriptor.getDeclaredLength();
   MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 
                                             startOffset, 
                                             declaredLength);
   interpreter = new Interpreter(buffer);
   ```

3. 使用

   ```java
   int[] input = {1}; // 输入 1
   int[] output = {0}; // 输出 默认值为0
   interpreter.run(input, output); // 使用模型计算
   // 这里input和output数据类型一定要和模型训练的时候一致：from_session(sess, [input], [output]) 
   // 这里[]为张量，一种数据结构，可以看作数组的数组
   ```

   ```
   经过模型的计算output[0]已经变成了2
   ```

![tensorflow demo](https://i.loli.net/2019/06/12/5d00c7a1715c444322.png)