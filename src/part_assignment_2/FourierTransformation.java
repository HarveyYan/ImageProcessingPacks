package part_assignment_2;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import part_assignment_1.Interface;

public class FourierTransformation implements ActionListener{

	private Interface interfaceRef;
	
	public FourierTransformation(Interface interfaceRef) {
		this.interfaceRef = interfaceRef;
	}
	
	//移位倒转
    public static int Reverse(int n,int r){
        int tmp=0;
        for(int i=r-1;i>=0&&n!=0;i--){
            tmp+=(n%2)<<i;
            n/=2;
        }
        return tmp;
    }
	
	//一维快速傅里叶变换,src为原数据，r为迭代阶数，N为扩充后总的节点数
    public static ComplexNumber[] FFT(ComplexNumber[] src,int N,int r)
    {
        ComplexNumber[] des=new ComplexNumber[N];//最后结果
        ComplexNumber[] tmp=new ComplexNumber[N];//中间暂存
        int i,j,k;//循环变量
        int step;//间隔
        int wn;//用于计算W使用
        double x;//用于计算W使用
        ComplexNumber W=new ComplexNumber();
        ComplexNumber Odd=new ComplexNumber();//第二部分
        //调整顺序
        for(i=0;i<N;i++){
            j=Reverse(i,r);
            des[j]=src[i];
        }
        //FFT
        for(i=0;i<r;i++){//最外层嵌套，共r次迭代
            step=1<<i;//间隔为1，2，4，……2^(r-1)
            wn=step*2;
            for(j=0;j<N/wn;j++)
                for(k=0;k<step;k++){
                    x=2*Math.PI*k/wn;
                    W.real=Math.cos(x);W.img=-Math.sin(x);
                    Odd=W.mul(des[j*wn+step+k]);
                    tmp[j*wn+k]=des[j*wn+k].add(Odd);
                    tmp[j*wn+step+k]=des[j*wn+k].sub(Odd);
                }
            //将tmp回填到des中，一遍继续循环使用
            for(int cou=0;cou<N;cou++)
            {
                des[cou]=tmp[cou];  
            }
        }
        return des;
    }
	
	private void FastFourierCaller() 
	{
        // TODO add your handling code here:
      int m=interfaceRef.imageHeight;
      int n=interfaceRef.imageWidth;
      int Mr,Nr;//迭代次数
      for(Mr=1;!(Math.pow(2, Mr-1)<=m&&Math.pow(2, Mr)>m);Mr++) 
          ;
      //扩展后行数
      int M=(int)Math.pow(2, Mr);
      for(Nr=1;!(Math.pow(2, Nr-1)<=n&&Math.pow(2, Nr)>n);Nr++) 
          ;
      //扩展后列数
      int N=(int)Math.pow(2, Nr);
      
      //扩展后的单点傅立叶变换复数矩阵
      ComplexNumber [][] Ext=new ComplexNumber[M][N];
      //X行的一维傅立叶变换
      ComplexNumber [][] ExtX=new ComplexNumber[M][N];
      //二维傅里叶变换
      ComplexNumber [][] ExtXY=new ComplexNumber[M][N];
       //初始化矩阵
      for(int i=0;i<M;i++)
          for(int j=0;j<N;j++)
              Ext[i][j]=new ComplexNumber();
      //将原图片填入Ext矩阵
      for(int i=0;i<m;i++)
          for(int j=0;j<n;j++)
              Ext[i][j]=new ComplexNumber(interfaceRef.pixels[j + i*interfaceRef.imageWidth]&0xff,0);
      
      //每行的一维FFT
      ComplexNumber [] tmp=new ComplexNumber[N];//暂存每一行的值
      for(int i=0;i<M;i++){
         for(int j=0;j<N;j++)
              tmp[j]=new  ComplexNumber(Ext[i][j].real,Ext[i][j].img);
          tmp=FFT(tmp,N,Nr);
          for(int j=0;j<N;j++)
          {
              ExtX[i][j]=new  ComplexNumber(tmp[j].real,tmp[j].img);
          }
      }
    
      //二维FFT
      ComplexNumber [] tmp2=new ComplexNumber[M];//暂存每一列的值
      for(int i=0;i<N;i++){
          for(int j=0;j<M;j++)
              tmp2[j]=new  ComplexNumber(ExtX[j][i].real,ExtX[j][i].img);
          tmp2=FFT(tmp2,M,Mr);
          for(int j=0;j<M;j++)
          {
              ExtXY[j][i]=new  ComplexNumber(tmp2[j].real,tmp2[j].img);
          }   
      }
      
        int gray,rgb;
        int[][] ddpi=new int[M][N];
        //将傅立叶变换频谱填入平移后位置，使其在中心显示
        for(int i=0;i<M;i++){
            for(int j=0;j<N;j++){
                gray=(int)Math.sqrt(Math.pow(ExtXY[i][j].real, 2)+Math.pow(ExtXY[i][j].img, 2))/100;
                if(gray>255) gray=255;
                ddpi[i<M/2?i+M/2:i-M/2][j<N/2?j+N/2:j-N/2]=gray;
            }
        }
        interfaceRef.saveHistory();
        interfaceRef.pixels = new int[M*N];
        //将灰度填入处理后的图片
        for(int i=0;i<M;i++){
            for(int j=0;j<N;j++){
                gray=ddpi[i][j];
                if (gray > 255)	gray = 255;
                Color c = new Color(gray,gray,gray);
                interfaceRef.pixels[j + i*interfaceRef.imageWidth]=c.getRGB();
            }
        }
         
         //绘制转换后图像区域
        interfaceRef.overrideImage();
    }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		FastFourierCaller();
	}
	
}
