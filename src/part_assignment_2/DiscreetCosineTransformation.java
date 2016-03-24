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


public class DiscreetCosineTransformation implements ActionListener{

	private Interface interfaceRef;
	
	public DiscreetCosineTransformation(Interface interfaceRef){
		this.interfaceRef = interfaceRef;
	}
	
    double[] DCT(double[] src,int N,int r){
        double [] des=new double[N];
        ComplexNumber [] X=new ComplexNumber[N*2];
        int i;
        for(i=0;i<N;i++)
            X[i]=new ComplexNumber(src[i],0);
        while(i<N*2)
            X[i++]=new ComplexNumber();
        
        X=FourierTransformation.FFT(X,N*2,r+1);
        double dtmp=1/Math.sqrt(N);
        des[0]=X[0].real*dtmp;
        dtmp*=Math.sqrt(2);
        for(i=1;i<N;i++)
            des[i]=(X[i].real*Math.cos(i*Math.PI /(N*2))+X[i].img*Math.sin(i*Math.PI /(N*2)))*dtmp;
        
        return des;
     }
    
    private void DiscreetConsineTransCaller(){
      int m=interfaceRef.imageHeight;
      int n=interfaceRef.imageWidth;
      int Mr,Nr;
      for(Mr=1;!(Math.pow(2, Mr-1)<=m&&Math.pow(2, Mr)>m);Mr++) ;
      int M=(int)Math.pow(2, Mr);
      for(Nr=1;!(Math.pow(2, Nr-1)<=n&&Math.pow(2, Nr)>n);Nr++) 
          ;
      //扩展后列数
      int N=(int)Math.pow(2, Nr);
      
      //扩展后的单点傅立叶变换复数矩阵
      double [][] Ext=new double[M][N];
      //X行的一维傅立叶变换
      double [][] ExtX=new double[M][N];
      //二维傅里叶变换
      double [][] ExtXY=new double[M][N];
       //初始化矩阵
      for(int i=0;i<M;i++)
          for(int j=0;j<N;j++)
              Ext[i][j]=0;
      //将原图片填入Ext矩阵
      for(int i=0;i<m;i++)
          for(int j=0;j<n;j++)
              Ext[i][j]=Ext[i][j]=interfaceRef.pixels[j + i*interfaceRef.imageWidth]&0xff;
      
      //每行的一维DCT
      double [] tmp=new double[N];//暂存每一行的值
      for(int i=0;i<M;i++){
         for(int j=0;j<N;j++)
              tmp[j]=Ext[i][j];
          tmp=DCT(tmp,N,Nr);
          for(int j=0;j<N;j++)
          {
              ExtX[i][j]=tmp[j];
          }
      }
    
      //二维DCT
      double [] tmp2=new double[M];//暂存每一列的值
      for(int i=0;i<N;i++){
          for(int j=0;j<M;j++)
              tmp2[j]=ExtX[j][i];
          tmp2=DCT(tmp2,M,Mr);
          for(int j=0;j<M;j++)
          {
              ExtXY[j][i]=tmp2[j];
          }   
      }
      
        int gray,rgb;
        int[][] ddpi=new int[M][N];
   
        interfaceRef.saveHistory();
        interfaceRef.pixels = new int[M*N];
        
        for(int i=0;i<M;i++){
            for(int j=0;j<N;j++){
                gray=Math.abs((int)ExtXY[i][j]);
                if (gray > 255)	gray = 255;
                Color c = new Color(gray,gray,gray);
                interfaceRef.pixels[j + i*interfaceRef.imageWidth]=c.getRGB();
            }
        }
        interfaceRef.overrideImage();
         
    }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		DiscreetConsineTransCaller();
	}

}
