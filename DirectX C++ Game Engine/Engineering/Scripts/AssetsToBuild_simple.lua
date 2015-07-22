return
{
	-- Generic Assets
	-- (That just get copied as-is rather than built)
	generic =
	{
		builder = "GenericBuilder.exe",
		assets =
		{
			{
				name = "Grass_Material.txt",
				extension = "Grass_Material.txt",
			},
			{
				name = "Models/Mario_Material.txt",
				extension = "Models/Mario_Material.txt",
			},
			{
				name = "Star_Material.txt",
				extension = "Star_Material.txt",
			},
			{
				name = "material_floor.txt",
				extension = "material_floor.txt",
			},
			{
				name = "material_railing.txt",
				extension = "material_railing.txt",
			},
			{
				name = "material_metal.txt",
				extension = "material_metal.txt",
			},
			{
				name = "material_walls.txt",
				extension = "material_walls.txt",
			},
			{
				name = "material_cement.txt",
				extension = "material_cement.txt",
			},
			{
				name = "light_settings.txt",
				extension = "light_settings.txt",
			},
		},

	},
	shader =
	{
		builder = "ShaderBuilder.exe",
		assets = 
		{
			{
				name = "vertexShader.hlsl",
				extension = "vertexShader.bsl",
				arguments = { "vertex" },
			},
			{
				name = "fragmentShader.hlsl",
				extension = "fragmentShader.bsl",
				arguments = { "fragment" },
			},
			{
				name = "vertexShader_Sprite.hlsl",
				extension = "vertexShader_Sprite.bsl",
				arguments = { "vertex" },
			},
			{
				name = "fragmentShader_Sprite.hlsl",
				extension = "fragmentShader_Sprite.bsl",
				arguments = { "fragment" },
			},
			{
				name = "vertexShader_DebugLine.hlsl",
				extension = "vertexShader_DebugLine.bsl",
				arguments = { "vertex" },
			},
			{
				name = "fragmentShader_DebugLine.hlsl",
				extension = "fragmentShader_DebugLine.bsl",
				arguments = { "fragment" },
			},
		},
	},
	mesh =
	{
		builder = "MeshBuilder.exe",
		assets = 
		{
			{
				name = "Plane_Floor.mesh",
				extension = "Plane_Floor.mesh",
			},
			{
				name = "Models/Mario.mesh",
				extension = "Models/Mario.mesh",
			},
			{

				name = "cube.mesh",
				extension = "cube.mesh",
			},
			{
				name = "sphere.mesh",
				extension = "sphere.mesh",
			},
			{
				name = "plane.mesh",
				extension = "plane.mesh",
			},
			{
				name = "Floor_Mesh.mesh",
				extension = "Floor_Mesh.mesh",
			},
			{
				name = "Railings.mesh",
				extension = "Railings.mesh",
			},
			{
				name = "metal_bars.mesh",
				extension = "metal_bars.mesh",
			},
			{
				name = "Walls.mesh",
				extension = "Walls.mesh",
			},
			{
				name = "Ceilings.mesh",
				extension = "Ceilings.mesh",
			},
			{
				name = "Cement_Wall.mesh",
				extension = "Cement_Wall.mesh",
			},
			{
				name = "Floor_Mesh.mesh",
				extension = "Floor_Mesh.mesh",
			},
			{
				name = "Railings.mesh",
				extension = "Railings.mesh",
			},
			{
				name = "metal_bars.mesh",
				extension = "metal_bars.mesh",
			},
			{
				name = "Walls.mesh",
				extension = "Walls.mesh",
			},
			{
				name = "Ceilings.mesh",
				extension = "Ceilings.mesh",
			},
			{
				name = "Cement_Wall.mesh",
				extension = "Cement_Wall.mesh",
			},
			{
				name = "RandomObjects.mesh",
				extension = "RandomObjects.mesh",
			},
		},
	},
	texture = 
	{
		builder = "TextureBuilder.exe",
		assets = 
		{
			{
				name = "wood.jpg",
				extension = "wood.dds",
			},
			{
				name = "logo.png",
				extension = "logo.dds",
			},
			{
				name = "numbers.png",
				extension = "numbers.dds",
			},
			{
				name = "runningcat.png",
				extension = "runningcat.dds",
			},
			{
				name = "got.png",
				extension = "got.dds",
			},
			{
				name = "Grass.jpg",
				extension = "Grass.dds",
			},
			{
				name = "GrassNormal.jpg",
				extension = "GrassNormal.dds",
			},
			--Mario
			{
				name = "Models/marioD.jpg",
				extension = "Models/marioD.dds",
			},
			{
				name = "stars.jpg",
				extension = "stars.dds",
			},
			{
				name = "metal.jpg",
				extension = "metal.dds",
			},
			{
				name = "floor_D.png",
				extension = "floor_D.dds",
			},
			{
				name = "railing_D.png",
				extension = "railing_D.dds",
			},
			{
				name = "metal_brace_D.png",
				extension = "metal_brace_D.dds",
			},
			{
				name = "wall_D.png",
				extension = "wall_D.dds",
			},
			{
				name = "cement_wall_D.png",
				extension = "cement_wall_D.dds",
			},
		}
	}
}
