package autobots.parsing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class Parser {
	public static String relativePath = "..\\autobots\\src\\main\\resources\\";
//	public static String relativePath = ".\\";
//	public static String relativePath = "";

	public static XmlFluxUsdt loadXml() {
		File xmlFile = new File(relativePath + "xmlFluxUsdt.xml");

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(XmlFluxUsdt.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			XmlFluxUsdt xmlFluxUsdt = (XmlFluxUsdt) jaxbUnmarshaller.unmarshal(xmlFile);
			return xmlFluxUsdt;

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<Double> loadMa30(XmlFluxUsdt xmlFluxUsdt) {
		ArrayList<Double> fluxUsdtMa30 = new ArrayList<Double>(Arrays.asList(xmlFluxUsdt.getMa1(), xmlFluxUsdt.getMa2(),
				xmlFluxUsdt.getMa3(), xmlFluxUsdt.getMa4(), xmlFluxUsdt.getMa5(), xmlFluxUsdt.getMa6(),
				xmlFluxUsdt.getMa7(), xmlFluxUsdt.getMa8(), xmlFluxUsdt.getMa9(), xmlFluxUsdt.getMa10(),
				xmlFluxUsdt.getMa11(), xmlFluxUsdt.getMa12(), xmlFluxUsdt.getMa13(), xmlFluxUsdt.getMa14(),
				xmlFluxUsdt.getMa15(), xmlFluxUsdt.getMa16(), xmlFluxUsdt.getMa17(), xmlFluxUsdt.getMa18(),
				xmlFluxUsdt.getMa19(), xmlFluxUsdt.getMa20(), xmlFluxUsdt.getMa21(), xmlFluxUsdt.getMa22(),
				xmlFluxUsdt.getMa23(), xmlFluxUsdt.getMa24(), xmlFluxUsdt.getMa25(), xmlFluxUsdt.getMa26(),
				xmlFluxUsdt.getMa27(), xmlFluxUsdt.getMa28(), xmlFluxUsdt.getMa29(), xmlFluxUsdt.getMa30()));

		System.out.println("ma30 valeurs de depart : " + fluxUsdtMa30.toString());
		return fluxUsdtMa30;
	}

	public static ArrayList<Double> loadMa30Trending(XmlFluxUsdt xmlFluxUsdt) {

		ArrayList<Double> fluxUsdtTrend = new ArrayList<Double>(Arrays.asList(xmlFluxUsdt.getTrend1(),
				xmlFluxUsdt.getTrend2(), xmlFluxUsdt.getTrend3(), xmlFluxUsdt.getTrend4(), xmlFluxUsdt.getTrend5(),
				xmlFluxUsdt.getTrend6(), xmlFluxUsdt.getTrend7(), xmlFluxUsdt.getTrend8(), xmlFluxUsdt.getTrend9(),
				xmlFluxUsdt.getTrend10(), xmlFluxUsdt.getTrend11(), xmlFluxUsdt.getTrend12(), xmlFluxUsdt.getTrend13(),
				xmlFluxUsdt.getTrend14(), xmlFluxUsdt.getTrend15(), xmlFluxUsdt.getTrend16(), xmlFluxUsdt.getTrend17(),
				xmlFluxUsdt.getTrend18(), xmlFluxUsdt.getTrend19(), xmlFluxUsdt.getTrend20(), xmlFluxUsdt.getTrend21(),
				xmlFluxUsdt.getTrend22(), xmlFluxUsdt.getTrend23(), xmlFluxUsdt.getTrend24(), xmlFluxUsdt.getTrend25(),
				xmlFluxUsdt.getTrend26(), xmlFluxUsdt.getTrend27(), xmlFluxUsdt.getTrend28(), xmlFluxUsdt.getTrend29(),
				xmlFluxUsdt.getTrend30()));

		System.out.println("trend valeurs de depart : " + fluxUsdtTrend.toString());
		return fluxUsdtTrend;
	}

	public static String createFile(String fileName, String extension) {
		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
			LocalDateTime now = LocalDateTime.now();
			String date = dtf.format(now);
			String name = relativePath + fileName + date + extension;
			File myObj = new File(name);
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.out.println("File already exists.");
			}
			return name;
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
			return "error_in_file_creation";
		}
	}

	public static void saveData(ArrayList<Double> ma30, ArrayList<Double> trend) {
		String fileName = createFile("xmlFluxUsdt", ".xml");
		try {
			FileWriter myWriter = new FileWriter(fileName);
			myWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
			myWriter.write("\n");
			myWriter.write("<xmlFluxUsdt>");
			myWriter.write("\n");

			for (int i = 0; i < ma30.size(); i++) {
				myWriter.write("\t<ma" + i + ">" + ma30.get(i) + "</ma" + i + ">");
				myWriter.write("\n");
			}
			for (int i = 0; i < trend.size(); i++) {
				myWriter.write("\t<trend" + i + ">" + trend.get(i) + "</trend" + i + ">");
				myWriter.write("\n");
			}
			myWriter.write("</xmlFluxUsdt>");
			myWriter.write("\n");
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public static String getDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String date = dtf.format(now);
		return date;
	}

	public static void write(FileWriter myWriter, String msg) {
		try {
			String date = getDate();
			myWriter.write("[" + date + "]   " + msg);
			System.out.println(msg);
			myWriter.write("\n");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}
