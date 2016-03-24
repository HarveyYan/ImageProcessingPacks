package part_assignment_2;

public class ComplexNumber {
    public double real;
    public double img;
    
    public  ComplexNumber(){
        real=0;
        img=0;
    }
    public  ComplexNumber(double r,double i){
        real=r;
        img=i;
    }
    public ComplexNumber add(ComplexNumber t){
        ComplexNumber tmp=new ComplexNumber();
        tmp.real=this.real+t.real;
        tmp.img=this.img+t.img;
        return tmp;
    }
    public ComplexNumber sub(ComplexNumber t){
        ComplexNumber tmp=new ComplexNumber();
        tmp.real=this.real-t.real;
        tmp.img=this.img-t.img;
        return tmp;
    }
    public ComplexNumber mul(ComplexNumber t){
        ComplexNumber tmp=new ComplexNumber();
        tmp.real=this.real*t.real-this.img*t.img;
        tmp.img=this.real*t.img+this.img*t.real;
        return tmp;
    }
}
