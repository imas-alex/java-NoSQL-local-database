package gdt.data.entity;

public class Bond {
  public String inNodeKey$;
  public String outNodeKey$;
  public String bondKey$;
  public String edgeKey$;
  public Bond(){};
  public Bond(String inNodeKey$,String outNodeKey$,String bondKey$,String edgeKey$){
	  this.inNodeKey$=inNodeKey$;
	  this.outNodeKey$=outNodeKey$;
	  this.bondKey$=bondKey$;
	  this.edgeKey$=edgeKey$;
  };
}
