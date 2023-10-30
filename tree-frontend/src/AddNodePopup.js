import React from "react";
import "./App.css";

import 'reactjs-popup/dist/index.css';
import {callAddNode, setNewRoot} from "./helpers";
import 'react-notifications/lib/notifications.css';
import {createNotification} from "./App";

export default function AddNodePopup(props) {
    const [isNewRoot, setIsNewRoot] = React.useState(false);
    const [value, setValue] = React.useState(0)
    const [parentId, setParentId] = React.useState(0)

    const [modified, setModified] = React.useState(null)
    const [response, setResponse] = React.useState(null)

    const handleNewRoot = () => {
        setIsNewRoot(!isNewRoot);
    };
    const handleSubmit = (e) => {
        let newNode = {value:value, parentId: !isNewRoot ? parentId : null}
        e.preventDefault()
        callAddNode(newNode, setResponse, props.setData, props.close)
        // setNewRoot(props.treeData, response.modified, props.setData)
    }

    if(response != null){
        console.log("Response is not null: ", response)

        console.log("Modified: ", modified)
        setNewRoot(props.treeData, response.modified, props.setData, props.setLoading)
        setResponse(null)
        props.close()
        createNotification("success", "New node created")
    }

    return (

        <div>
            {modified != null ? ()=>{console.log("Modified ",modified)} : ()=>{}}
            <h2>Add new node</h2>
            <div>
                <form name={"addNode"} onSubmit={handleSubmit}>
                    <label>
                        <input type={"number"} name={"value"} value={value} onChange={(v) => {
                            setValue(parseInt(v.target.value))
                        }}/>
                        Node value
                    </label>
                    <br/>
                    {!isNewRoot &&
                        <div>
                            <label>
                                <input type={"number"} name={"parentId"} value={parentId} onChange={(v) => {
                                    setParentId(parseInt(v.target.value))
                                }}/>
                                ID of parent node
                            </label>
                            <br/>
                        </div>

                    }
                    <label>
                        <input
                            type="checkbox"
                            checked={isNewRoot}
                            onChange={handleNewRoot}
                        />
                        Is new root?
                    </label>
                    <br/>
                    <input type={"submit"} value={"Create"}/>
                </form>
            </div>
        </div>
    );
}