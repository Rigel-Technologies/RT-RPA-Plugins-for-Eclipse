package rpa_suite_for_eclipse.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;

//Para la gestión de la variable XML del doCommand --------
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
//---------------------------------------------------------

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import java.io.*;

//Importamos el objeto del Robot Cartes --
import CartesObj.*;
//----------------------------------------

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "rpa". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class SampleNewWizard extends Wizard implements INewWizard {
	private SampleNewWizardPage page;
	private ISelection selection;
	private String resultCreateProjectDeveloper = null;
	private String pathCreateProjectRPA = null;

	/**
	 * Constructor for SampleNewWizard.
	 */
	public SampleNewWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */
	@Override
	public void addPages() {
		page = new SampleNewWizardPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	@Override
	public boolean performFinish() 
	{
		final String containerName = page.getContainerName();
		final String fileName = page.getFileName();
		IRunnableWithProgress op = monitor -> {
			try {
				doFinish(containerName, fileName, monitor);
			} catch (CoreException e) {
				throw new InvocationTargetException(e);
			} finally {
				monitor.done();
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		
		return true;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */

	private void doFinish(String containerName, String fileName, IProgressMonitor monitor) throws CoreException 
	{
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		//System.out.println("resource.getFullPath() --->"+resource.getFullPath());
		
		//throwCoreException(System.getProperty("java.home"));
		
		
		//Se valida que el contenedor o carpeta donde se ha de crear el archivo .rpa exista --
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}
		//-------------------------------------------------------------------------------------
		
		
		//Validamos que el java_home no se encuentre en la unidad C:\Program File, de estar acá
		//no podremos copiar las dll por falta de permisología administrativa -----------------------------
		int validatorJavaHome = 0;
		validatorJavaHome = System.getProperty("java.home").indexOf("Program");
		//throwCoreException(System.getProperty("java.home") + "validatorJavaHome --->"+validatorJavaHome);
		if(validatorJavaHome > 0)
		{
			String pathJavaHome = System.getProperty("java.home") + "\\bin\\";
			File archivo1 = new File(pathJavaHome + "com4j-amd64.dll");
			File archivo2 = new File(pathJavaHome + "com4j-x86.dll");
			File archivo3 = new File(pathJavaHome + "com4j.dll");
			if (!archivo1.exists() || !archivo2.exists() || !archivo3.exists()) {
			    System.out.println("OJO: ¡¡No existe el archivo de configuración!!");
			    throwCoreException("Since you are trying to use the plugin (RPA Suite for Eclipse) in a version of Eclipse lower than the year 2021, you must copy the following files (com4j-amd64.dll, com4j-x86.dll and com4j.dll) in the following path (java_home :" + System.getProperty("java.home") + "\\bin\\), these files can be found in the directory of RPA Suite installation (C:\\Program Files (x86)\\Rigel Technologies\\Client\\).");
			}
			
		}
		//-------------------------------------------------------------------------------------------------------------

		
		if(validatorJavaHome < 0)
		{
			//Para copiar la dll de com4j-amd64.dll -----------------------------------------------------------------------
			InputStream streamincom4jamd64 = SampleNewWizard.class.getResourceAsStream("/com4j-amd64.dll");
		    streamincom4jamd64 = SampleNewWizard.class.getClassLoader().getResourceAsStream("com4j-amd64.dll");
			byte[] buffercom4jamd64 = new byte[16384];
			InputStream incom4jamd64 = null;
			incom4jamd64 = streamincom4jamd64;
			OutputStream outstreamincom4jamd64 = null;
			try {
				outstreamincom4jamd64 = new FileOutputStream(System.getProperty("java.home")+"/bin/com4j-amd64.dll");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				throwCoreException(e1.toString());
			}
			while (true) 
			{
				int n = 0;
				try {
					n = streamincom4jamd64.read(buffercom4jamd64);
				} catch (IOException e) {
					//e.printStackTrace();
					throwCoreException(e.toString());
				}
				if (n == -1)
					break;
				try {
					outstreamincom4jamd64.write(buffercom4jamd64, 0, n);
				} catch (IOException e) {
					e.printStackTrace();
					throwCoreException(e.toString());
				}
			}
			try {
				incom4jamd64.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				throwCoreException(e1.toString());
			}
			try {
				outstreamincom4jamd64.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				throwCoreException(e1.toString());
			}
			//System.out.println("Termino de escribir dll com4j-amd64.dll");
			//--------------------------------------------------------------------------------
			
			//Para copiar la dll de com4j-x86.dll -----------------------------------------------------------------------
			InputStream streamcom4jx86 = SampleNewWizard.class.getResourceAsStream("/com4j-x86.dll");
		    streamcom4jx86 = SampleNewWizard.class.getClassLoader().getResourceAsStream("com4j-x86.dll");
			byte[] buffercom4jx86 = new byte[16384];
			InputStream incom4jx86 = null;
			incom4jx86 = streamcom4jx86;
			OutputStream outcom4jx86 = null;
			try {
				outcom4jx86 = new FileOutputStream(System.getProperty("java.home")+"/bin/com4j-x86.dll");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				throwCoreException(e1.toString());
			}
			while (true) 
			{
				int n = 0;
				try {
					n = streamcom4jx86.read(buffercom4jx86);
				} catch (IOException e) {
					e.printStackTrace();
					throwCoreException(e.toString());
				}
				if (n == -1)
					break;
				try {
					outcom4jx86.write(buffercom4jx86, 0, n);
				} catch (IOException e) {
					e.printStackTrace();
					throwCoreException(e.toString());
				}
			}
			try {
				incom4jx86.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				throwCoreException(e1.toString());
			}
			try {
				outcom4jx86.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				throwCoreException(e1.toString());
			}
			//System.out.println("Termino de escribir dll com4j-x86.dll");
			//--------------------------------------------------------------------------------

			//Para copiar la dll de com4j-x86.dll -----------------------------------------------------------------------
			InputStream streamcom4j = SampleNewWizard.class.getResourceAsStream("/com4j.dll");
		    streamcom4j = SampleNewWizard.class.getClassLoader().getResourceAsStream("com4j.dll");
			byte[] buffercom4j = new byte[16384];
			InputStream incom4j = null;
			incom4j = streamcom4j;
			OutputStream outcom4j = null;
			try {
				outcom4j = new FileOutputStream(System.getProperty("java.home")+"/bin/com4j.dll");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				throwCoreException(e1.toString());
			}
			while (true) 
			{
				int n = 0;
				try {
					n = streamcom4j.read(buffercom4j);
				} catch (IOException e) {
					e.printStackTrace();
					throwCoreException(e.toString());
				}
				if (n == -1)
					break;
				try {
					outcom4j.write(buffercom4j, 0, n);
				} catch (IOException e) {
					e.printStackTrace();
					throwCoreException(e.toString());
				}
			}
			try {
				incom4j.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				throwCoreException(e1.toString());
			}
			try {
				outcom4j.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				throwCoreException(e1.toString());
			}
			//System.out.println("Termino de escribir dll com4j.dll");
			//--------------------------------------------------------------------------------
		}

		//Proceso donde se crea el archivo dentro del contenedor seleccionado --
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		try {
			InputStream stream = openContentStream();
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
			pathCreateProjectRPA = file.getLocation().toString();
			
		} catch (IOException e) {
			throwCoreException(e.toString());
		}
		//-----------------------------------------------------------------------
		
		//Gestión de creación del proyecto .rpa en el RPA Developer ---------------------------------------------------------------------
		String installed = null;
		String runned = null;
		System.out.println("1");
		IRPADeveloper rpaDeveloper = ClassFactory.createRPADeveloper();
		System.out.println("2");
		rpaDeveloper.reset();
		rpaDeveloper.savetofile(pathCreateProjectRPA);
		resultCreateProjectDeveloper = rpaDeveloper.doCommand("Open Developer", "<rpa><file>" + pathCreateProjectRPA.replace("/", "\\") + "</file><caller>msvs</caller></rpa>");
		
		//Validamos si la ejecucción del doCommand del RPA Developer devuelve una cadena válida de respuesta  ----
		if(resultCreateProjectDeveloper != null)
		{
			byte[] xml = resultCreateProjectDeveloper.getBytes();
			try {
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml));
				
				//Obtenemos el valor del tag installed; 1 - instalado / 0 - No instalado -- 
				NodeList list = doc.getElementsByTagName("installed");
				NodeList subList = list.item(0).getChildNodes();
				installed = subList.item(0).getNodeValue().toString();
				//System.out.println(" installed --->"+subList.item(0).getNodeValue());
				//----------------------------------------------------------------------

				//Obtenemos el valor del tag runned; 1 - corriendo / 0 - No corriendo -- 
				NodeList list1 = doc.getElementsByTagName("runned");
				NodeList subList1 = list1.item(0).getChildNodes();
				runned = subList1.item(0).getNodeValue().toString();
				//System.out.println(" runned --->"+subList1.item(0).getNodeValue());
				//----------------------------------------------------------------------
				
				if(installed.equals("1"))
				{
					if(runned.equals("0"))
						throwCoreException("You need to have an RPA project to open RPA Developer.");
				}else
					throwCoreException("Please install RPA Developer. This interface is deprecated.");

				
			} catch (SAXException e) {
				e.printStackTrace();
				throwCoreException(e.toString());
			} catch (IOException e) {
				e.printStackTrace();
				throwCoreException(e.toString());
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				throwCoreException(e.toString());
			}
		}
		else
		{
			throwCoreException("Please install RPA Developer. This interface is deprecated.");
		}
		//----------------------------------------------------------------------------------------------------------------------------
	}
	
	/**
	 * We will initialize file contents with a sample text.
	 */

	private InputStream openContentStream() {
		String contents =
			"This is the initial file contents for *.rpa file that should be word-sorted in the Preview page of the multi-page editor";
		return new ByteArrayInputStream(contents.getBytes());
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "RPA_Suite_for_Eclipse", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}