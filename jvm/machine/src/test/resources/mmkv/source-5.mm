[define,edge:('outV'->int,'label'->str,'inV'->int)]
[define,vertex<=mmkv:('k'->int,'v'->person:('name'->str,'age'->int))<x>-<
  ('id'      -> .k,
   'name'    -> .v.name,
   'friends' -> [=mmkv,''].v[is,[a,edge]][is,.outV==<.x>.k]
  )]
[define,vertex<=int<x>[=mmkv,''][is,.k==x]]