package W14for4;

import java.util.ArrayList;
import java.util.Collection;

public class T {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Collection monster = new ArrayList();
		monster.add("flower");
		monster.add("cloud");
		monster.add("dog");
		System.out.println("Monster�`��: "+monster.size());

		System.out.println("�M��flower Monster: "+monster.contains("flower"));

		System.out.println("�C�X�Ҧ�Monsters "+monster);
	}

}
