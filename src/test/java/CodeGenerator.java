import static com.company.project.core.ProjectConstant.BASE_PACKAGE;
import static com.company.project.core.ProjectConstant.CONTROLLER_PACKAGE;
import static com.company.project.core.ProjectConstant.MAPPER_INTERFACE_REFERENCE;
import static com.company.project.core.ProjectConstant.MAPPER_PACKAGE;
import static com.company.project.core.ProjectConstant.MODEL_PACKAGE;
import static com.company.project.core.ProjectConstant.SERVICE_IMPL_PACKAGE;
import static com.company.project.core.ProjectConstant.SERVICE_PACKAGE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.DefaultShellCallback;

import com.google.common.base.CaseFormat;

import freemarker.template.TemplateExceptionHandler;

/**
 * 代码生成器，根据数据表名称生成对应的Model、Mapper、Service、Controller简化开发。
 */
public class CodeGenerator {
	// JDBC配置，请修改为你项目的实际配置
	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/power";
	private static final String JDBC_USERNAME = "root";
	private static final String JDBC_PASSWORD = "";
	private static final String JDBC_DIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

	private static final String PROJECT_PATH = System.getProperty("user.dir");// 项目在硬盘上的基础路径
	private static final String TEMPLATE_FILE_PATH = PROJECT_PATH + "/src/test/resources/generator/template";// 模板位置

	private static final String JAVA_PATH = "/src/main/java"; // java文件路径
	private static final String RESOURCES_PATH = "/src/main/resources";// 资源文件路径

	private static final String PACKAGE_PATH_SERVICE = packageConvertPath(SERVICE_PACKAGE);// 生成的Service存放路径
	private static final String PACKAGE_PATH_SERVICE_IMPL = packageConvertPath(SERVICE_IMPL_PACKAGE);// 生成的Service实现存放路径
	private static final String PACKAGE_PATH_CONTROLLER = packageConvertPath(CONTROLLER_PACKAGE);// 生成的Controller存放路径
	private static final String PACKAGE_PATH_DAO = packageConvertPath(MAPPER_PACKAGE);// 生成的Controller存放路径

	private static final String AUTHOR = "CodeGenerator";// @author
	private static final String DATE = new SimpleDateFormat("yyyy/MM/dd").format(new Date());// @date

	public static void main(String[] args) throws Exception {
		// 生成mapper dao service controller
		genCode("role_permission");
		// genCode("输入表名","输入自定义Model名称");
	}

	/**
	 * 通过数据表名称生成代码，Model 名称通过解析数据表名称获得，下划线转大驼峰的形式。 如输入表名称 "t_user_detail" 将生成
	 * TUserDetail、TUserDetailMapper、TUserDetailService ...
	 * 
	 * @param tableNames
	 *            数据表名称...
	 * @throws Exception
	 */
	public static void genCode(String... tableNames) throws Exception {
		for (String tableName : tableNames) {
			genCode(tableName, null);
		}
	}

	/**
	 * 通过数据表名称，和自定义的 Model 名称生成代码 如输入表名称 "t_user_detail" 和自定义的 Model 名称 "User"
	 * 将生成 User、UserMapper、UserService ...
	 * 
	 * @param tableName
	 *            数据表名称
	 * @param modelName
	 *            自定义的 Model 名称
	 * @throws Exception
	 */
	public static void genCode(String tableName, String modelName) throws Exception {
		genModelAndMapper(tableName, modelName);
		genService(tableName, modelName);
		genController(tableName, modelName);

		// 生成自定义的方法 添加到mapper dao service
		genMethod(tableName);
	}

	public static void genMethod(String tableName) throws Exception {
		String modelName = tableNameConvertUpperCamel(tableName) + "Mapper.xml";
		String pathname = PROJECT_PATH + RESOURCES_PATH + "/mapper/" + modelName;
		File mpperFile = new File(pathname);
		Document document = Jsoup.parse(mpperFile, "UTF-8");
		Element resultMap = document.getElementsByTag("resultMap").get(0);
		Elements elements = resultMap.children();
		List<Map<String, String>> columnList = new ArrayList<>();
		for (Element e : elements) {
			Map<String, String> data = new HashMap<>();
			String a = e.attr("column");
			String b = e.attr("property");
			data.put("a", a);
			data.put("b", b);
			columnList.add(data);
		}

		Map<String, Object> data = new HashMap<>();
		data.put("modelNameUpperCamel", tableNameConvertUpperCamel(tableName));
		data.put("basePackage", BASE_PACKAGE);
		data.put("columnList", columnList);
		data.put("tableName", tableName);

		File file = new File(pathname + "1");
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		freemarker.template.Configuration cfg = getConfiguration();
		FileWriter fWriter = new FileWriter(file);
		cfg.getTemplate("mapper-method.ftl").process(data, fWriter);
		fWriter.close();

		List<String> addLines = readFileByLines(pathname + "1");
		deleteFile(pathname + "1");
		List<String> mapperLines = readFileByLines(pathname);
		mapperLines.addAll(mapperLines.size() - 1, addLines);
		deleteFile(pathname);
		fileWriter(pathname, mapperLines);

	}

	public static void genModelAndMapper(String tableName, String modelName) throws Exception {
		Context context = new Context(ModelType.FLAT);
		context.setId("Potato");
		context.setTargetRuntime("MyBatis3Simple");
		context.addProperty(PropertyRegistry.CONTEXT_BEGINNING_DELIMITER, "`");
		context.addProperty(PropertyRegistry.CONTEXT_ENDING_DELIMITER, "`");

		JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
		jdbcConnectionConfiguration.setConnectionURL(JDBC_URL);
		jdbcConnectionConfiguration.setUserId(JDBC_USERNAME);
		jdbcConnectionConfiguration.setPassword(JDBC_PASSWORD);
		jdbcConnectionConfiguration.setDriverClass(JDBC_DIVER_CLASS_NAME);
		context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

		PluginConfiguration pluginConfiguration = new PluginConfiguration();
		pluginConfiguration.setConfigurationType("tk.mybatis.mapper.generator.MapperPlugin");
		pluginConfiguration.addProperty("mappers", MAPPER_INTERFACE_REFERENCE);
		context.addPluginConfiguration(pluginConfiguration);

		JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
		javaModelGeneratorConfiguration.setTargetProject(PROJECT_PATH + JAVA_PATH);
		javaModelGeneratorConfiguration.setTargetPackage(MODEL_PACKAGE);
		context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

		SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
		sqlMapGeneratorConfiguration.setTargetProject(PROJECT_PATH + RESOURCES_PATH);
		sqlMapGeneratorConfiguration.setTargetPackage("mapper");
		context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

		JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
		javaClientGeneratorConfiguration.setTargetProject(PROJECT_PATH + JAVA_PATH);
		javaClientGeneratorConfiguration.setTargetPackage(MAPPER_PACKAGE);
		javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
		context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

		TableConfiguration tableConfiguration = new TableConfiguration(context);
		tableConfiguration.setTableName(tableName);
		tableConfiguration.setDomainObjectName(modelName);
		tableConfiguration.setGeneratedKey(new GeneratedKey("id", "Mysql", true, null));
		context.addTableConfiguration(tableConfiguration);

		List<String> warnings;
		MyBatisGenerator generator;
		try {
			Configuration config = new Configuration();
			config.addContext(context);
			config.validate();

			boolean overwrite = true;
			DefaultShellCallback callback = new DefaultShellCallback(overwrite);
			warnings = new ArrayList<String>();
			generator = new MyBatisGenerator(config, callback, warnings);
			generator.generate(null);
		} catch (Exception e) {
			throw new RuntimeException("生成Model和Mapper失败", e);
		}

		if (generator.getGeneratedJavaFiles().isEmpty() || generator.getGeneratedXmlFiles().isEmpty()) {
			throw new RuntimeException("生成Model和Mapper失败：" + warnings);
		}
		if (StringUtils.isEmpty(modelName))
			modelName = tableNameConvertUpperCamel(tableName);
		System.out.println(modelName + ".java 生成成功");
		System.out.println(modelName + "Mapper.java 生成成功");
		// 给mapper 加上方法
		String daoFile = PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_DAO + modelName + "Mapper.java";
		List<String> daoLines = readFileByLines(daoFile);
		daoLines.add(1, "import java.util.List;");
		daoLines.add(daoLines.size() - 1, "public List<" + modelName + "> getBeanList(" + modelName + " bean);");
		deleteFile(daoFile);
		fileWriter(daoFile, daoLines);

		System.out.println(modelName + "Mapper.xml 生成成功");
	}

	public static void genService(String tableName, String modelName) {
		try {
			freemarker.template.Configuration cfg = getConfiguration();

			Map<String, Object> data = new HashMap<>();
			data.put("date", DATE);
			data.put("author", AUTHOR);
			String modelNameUpperCamel = StringUtils.isEmpty(modelName) ? tableNameConvertUpperCamel(tableName) : modelName;
			data.put("modelNameUpperCamel", modelNameUpperCamel);
			data.put("modelNameLowerCamel", tableNameConvertLowerCamel(tableName));
			data.put("basePackage", BASE_PACKAGE);

			String serviceFile = PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_SERVICE + modelNameUpperCamel + "Service.java";
			File file = new File(serviceFile);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			FileWriter fWriter = new FileWriter(file);
			cfg.getTemplate("service.ftl").process(data,fWriter);
			fWriter.close();
			System.out.println(modelNameUpperCamel + "Service.java 生成成功");
			// 给service 加上方法
			List<String> serviceLines = readFileByLines(serviceFile);
			serviceLines.add(1, "import java.util.List;");
			serviceLines.add(serviceLines.size() - 1, "public List<" + modelNameUpperCamel + "> getBeanList(" + modelNameUpperCamel + " bean);");
			deleteFile(serviceFile);
			fileWriter(serviceFile, serviceLines);

			String implFile = PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_SERVICE_IMPL + modelNameUpperCamel + "ServiceImpl.java";
			File file1 = new File(implFile);
			if (!file1.getParentFile().exists()) {
				file1.getParentFile().mkdirs();
			}
			FileWriter fWriter1 = new FileWriter(file1);
			cfg.getTemplate("service-impl.ftl").process(data, fWriter1);
			fWriter1.close();
			System.out.println(modelNameUpperCamel + "ServiceImpl.java 生成成功");
			// 给service 加上方法
			List<String> implList = readFileByLines(implFile);
			implList.add(1, "import java.util.List;");
			implList.add(implList.size() - 1, "}");
			implList.add(implList.size() - 2, "return " + tableNameConvertLowerCamel(tableName) + "Mapper.getBeanList(bean);");
			implList.add(implList.size() - 3, "public List<" + modelNameUpperCamel + "> getBeanList(" + modelNameUpperCamel + " bean) {");
			deleteFile(implFile);
			fileWriter(implFile, implList);

		} catch (Exception e) {
			throw new RuntimeException("生成Service失败", e);
		}
	}

	public static void genController(String tableName, String modelName) {
		try {
			freemarker.template.Configuration cfg = getConfiguration();

			Map<String, Object> data = new HashMap<>();
			data.put("date", DATE);
			data.put("author", AUTHOR);
			String modelNameUpperCamel = StringUtils.isEmpty(modelName) ? tableNameConvertUpperCamel(tableName) : modelName;
			data.put("baseRequestMapping", modelNameConvertMappingPath(modelNameUpperCamel));
			data.put("modelNameUpperCamel", modelNameUpperCamel);
			data.put("modelNameLowerCamel", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, modelNameUpperCamel));
			data.put("basePackage", BASE_PACKAGE);

			File file = new File(PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_CONTROLLER + modelNameUpperCamel + "Controller.java");
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			// cfg.getTemplate("controller-restful.ftl").process(data, new
			// FileWriter(file));
			cfg.getTemplate("controller.ftl").process(data, new FileWriter(file));

			System.out.println(modelNameUpperCamel + "Controller.java 生成成功");
		} catch (Exception e) {
			throw new RuntimeException("生成Controller失败", e);
		}

	}

	private static freemarker.template.Configuration getConfiguration() throws IOException {
		freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_23);
		cfg.setDirectoryForTemplateLoading(new File(TEMPLATE_FILE_PATH));
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
		return cfg;
	}

	private static String tableNameConvertLowerCamel(String tableName) {
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, tableName.toLowerCase());
	}

	private static String tableNameConvertUpperCamel(String tableName) {
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName.toLowerCase());

	}

	private static String tableNameConvertMappingPath(String tableName) {
		tableName = tableName.toLowerCase();// 兼容使用大写的表名
		return "/" + (tableName.contains("_") ? tableName.replaceAll("_", "/") : tableName);
	}

	private static String modelNameConvertMappingPath(String modelName) {
		String tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, modelName);
		return tableNameConvertMappingPath(tableName);
	}

	private static String packageConvertPath(String packageName) {
		return String.format("/%s/", packageName.contains(".") ? packageName.replaceAll("\\.", "/") : packageName);
	}

	/**
	 * 按行写入
	 * 
	 * @param fileName
	 * @param clist
	 * @throws IOException
	 */
	public static void fileWriter(String fileName, List<String> clist) throws IOException {
		// true 表示追加
		FileWriter fw = new FileWriter(fileName, true);
		BufferedWriter writer = new BufferedWriter(fw);
		Iterator<String> iterator = clist.iterator();

		while (iterator.hasNext()) {
			String next = iterator.next().toString();
			if (!"".equals(next)) {
				writer.write(next);
				writer.newLine();// 换行
			}
		}
		// 刷新缓冲区
		fw.flush();
		// 关闭文件流对象
		writer.close();
		fw.close();
	}

	/**
	 * 按行 读取
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static List<String> readFileByLines(String fileName) throws Exception {
		File file = new File(fileName);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String tempString = null;
		// 创建一个集合
		List<String> nums = new ArrayList<String>();
		// 按行读取文件内容，并存放到集合
		while ((tempString = reader.readLine()) != null) {
			nums.add(tempString);
		}
		reader.close();
		// 返回集合变量
		return nums;
	}

	/**
	 * 删除单个文件
	 *
	 * @param fileName
	 *            要删除的文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				System.out.println("删除单个文件" + fileName + "成功！");
				return true;
			} else {
				System.out.println("删除单个文件" + fileName + "失败！");
				return false;
			}
		} else {
			System.out.println("删除单个文件失败：" + fileName + "不存在！");
			return false;
		}
	}

}
