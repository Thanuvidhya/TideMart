export default function MarginInput({value=0,onChange=()=>{}}){return <input type='number' min='0' className='w-24 border rounded px-2 py-1' value={value} onChange={e=>onChange(+e.target.value)}/> }
