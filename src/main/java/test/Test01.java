package test;

/**
 * @author Ayuan
 * @Description: 测试类01
 * @date 2022/12/13 15:36
 */
public class Test01 {

    public static void main(String[] args) {
        Circle circle = new Circle();
        double circleArea = circle.area(3);
        System.out.println("圆形面积为:" + circleArea);

        Square square = new Square();
        double squareArea = square.area(2);
        System.out.println("正方形面积为:" + squareArea);
    }
}