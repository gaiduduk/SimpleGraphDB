import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		
		while(true) {
			System.out.println("1.������� �����������");
			System.out.println("2.������� � �����������");
			/*System.out.println("3.��������� ���� ������������ �����");
			System.out.println("4.��������� ���� �����");
			System.out.println("5.��������� ���� ������");*/
			System.out.println("6.�����");
			System.out.print("\n������� ����� ����:");
			
			int num = new Scanner(System.in).nextInt();
			boolean flag = false;
			
			switch(num) {
				case 1: CreateRep(); break;
				case 2: ToRep(); break;
				case 3: 
					if(new InfinityTest().StartTest()) 
						System.out.println("���� ������ �������.\n");
					else
						System.out.println("���� ���������� ��������.\n");
					break;
				case 4: 
					if(new NodesTest().StartTest()) 
						System.out.println("���� ������ �������.\n");
					else
						System.out.println("���� ���������� ��������.\n");
					break;
				case 5:
					if(new NodesTest().StartTest()) 
						System.out.println("���� ������ �������.\n");
					else
						System.out.println("���� ���������� ��������.\n");
					break;
				case 6: flag = true;  break;
			}
			
			if(flag) break;
			
		}
		
	}
	
	public static void CreateRep() {
		try {
			System.out.print("������� ���� ��� �������� �����������:");
			String path = new Scanner(System.in).nextLine();
			System.out.print("������� ������������ ������ �����:");
			int size = new Scanner(System.in).nextInt();
			new InfinityFile(path+"\\.meta",size);
			new InfinityFile(path+"\\.nodes",size);
			new InfinityFile(path+"\\.links",size);
			new InfinityFile("C:\\test\\.tree",size);
			System.out.println("����������� ������� ������");
		} catch (Throwable e) {
			System.out.println("\n"+e.getMessage()+"\n");
		}
	}
	
	public static void ToRep() {
		System.out.print("������� ���� � �����������:");
		String path = new Scanner(System.in).nextLine();
		
		while(true) {
			System.out.println("\n1.�������� ����");
			System.out.println("2.�������� ����(����������)");
			System.out.println("3.������� ������ �����");
			System.out.println("4.�����");
			System.out.print("\n������� ����� ����:");
			
			int num = new Scanner(System.in).nextInt();
			boolean flag = false;
			
			try {
				Tree tree = new Tree(path);
				
				switch(num) {
					case 1:
						System.out.print("������� ������:");
						String addStr = new Scanner(System.in).nextLine();
						tree.Add(addStr);
						break;
					case 2:
						System.out.print("������� ������:");
						String addS = new Scanner(System.in).nextLine();
						tree.AddSame(addS);
						break;
					case 3:
						System.out.print("������� �������������:");
						String id = new Scanner(System.in).nextLine();
						if(id.length()!=4){ System.out.println("������ ��������������� �������"); }
							
						String[] strs = tree.Read(id);
						for(int i=0;i<strs.length;i++)
							System.out.println(strs[i]);
						
						break;
					case 4: flag = true;  break;
				}
			}catch(Throwable e) {
				System.out.println("\n"+e.getMessage()+"\n");
			}
		
			if(flag) break;
			
			System.out.println("\n\n"+path+"\n");
			
		}
		
	}
	
	public static String to8byte(long val){
		String buffer = String.valueOf(val);
		for(int i=buffer.length();i<8;i++)
			buffer = "0" + buffer;
		return buffer;
	}
	
	public static void ClearDir(String Path) {
		File index = new File(Path);
		String[]entries = index.list();
		for(String s: entries){
		    File currentFile = new File(index.getPath(),s);
		    currentFile.delete();
		}
	}
	
}
