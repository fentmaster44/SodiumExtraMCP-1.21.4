### MCP 1.21.4 with Sodium & SodiumExtra
Like MCPSodium but with SodiumExtra integrated as well.

There is a small issue with me.flashyreese.mods.sodiumextra.client.render.vertex.formats.TextureVertex 
where it somehow manages to write a color to the memory when the format doesn't support that, as it uses POSITION_TEX,
so I just cut out the code that uses it.

Contributions are welcome.

### Creators:
* me
* TTpu3paK_6axMyTa (from my Sodium repo)