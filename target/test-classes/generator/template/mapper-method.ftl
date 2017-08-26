	
	<sql id="Base_Column_List">
        <#list columnList as column> 
			<#if column?hasNext>  
				${column.a} ,
			<#else> 
				${column.a} 
			</#if> 
		</#list> 
    </sql>
    
    <select id="getBeanList" resultMap="BaseResultMap" parameterType="${basePackage}.model.${modelNameUpperCamel}">
		select
		<include refid="Base_Column_List" />
		from ${tableName}
		<where>
			<#list columnList as column> 
				<if test="${column.b} != null">
					and ${column.a} =  ${r'#{'} ${column.b} ${r'}'}
				</if>
			</#list> 
		</where>
	</select>
